document.addEventListener('DOMContentLoaded', function() {
    // Get admin form elements
    const createResponseForm = document.getElementById('createResponseForm');
    const editResponseForm = document.getElementById('editResponseForm');
    const removeResponseForm = document.getElementById('removeResponseForm');
    
    const createAction = document.getElementById('createAction');
    const editAction = document.getElementById('editAction');
    const removeAction = document.getElementById('removeAction');
    
    const createForm = document.getElementById('createForm');
    const editForm = document.getElementById('editForm');
    const removeForm = document.getElementById('removeForm');
    
    const editId = document.getElementById('editId');
    const searchInput = document.getElementById('searchInput');
    
    // Show/hide forms based on radio selection
    if (createAction) {
        createAction.addEventListener('change', () => {
            if (createAction.checked) {
                createForm.classList.add('active');
                editForm.classList.remove('active');
                removeForm.classList.remove('active');
            }
        });
    }
    
    if (editAction) {
        editAction.addEventListener('change', () => {
            if (editAction.checked) {
                createForm.classList.remove('active');
                editForm.classList.add('active');
                removeForm.classList.remove('active');
            }
        });
    }
    
    if (removeAction) {
        removeAction.addEventListener('change', () => {
            if (removeAction.checked) {
                createForm.classList.remove('active');
                editForm.classList.remove('active');
                removeForm.classList.add('active');
            }
        });
    }
    
    // Store all responses for edit operation
    const responses = [];
    document.querySelectorAll('.response-card').forEach(card => {
        const id = card.querySelector('.response-badge').textContent;
        const keywords = card.querySelectorAll('.card-text')[0].textContent;
        const response = card.querySelectorAll('.card-text')[1].textContent;
        const linkElement = card.querySelector('.card-link');
        
        responses.push({
            id: id,
            keywords: keywords,
            response: response,
            linkText: linkElement ? linkElement.textContent : '',
            linkUrl: linkElement ? linkElement.getAttribute('href') : ''
        });
    });
    
    // Populate edit form when selecting a response
    if (editId) {
        editId.addEventListener('change', () => {
            const selectedId = editId.value;
            if (!selectedId) return;
            
            const response = responses.find(r => r.id === selectedId);
            if (response) {
                document.getElementById('editKeywords').value = response.keywords;
                document.getElementById('editResponse').value = response.response;
                document.getElementById('editLinkText').value = response.linkText;
                document.getElementById('editLinkUrl').value = response.linkUrl;
            }
        });
    }
    
    // Search functionality
    if (searchInput) {
        searchInput.addEventListener('input', () => {
            const searchTerm = searchInput.value.toLowerCase();
            document.querySelectorAll('.response-card').forEach(card => {
                const keywords = card.querySelectorAll('.card-text')[0].textContent.toLowerCase();
                const response = card.querySelectorAll('.card-text')[1].textContent.toLowerCase();
                const id = card.querySelector('.response-badge').textContent;
                
                if (keywords.includes(searchTerm) || response.includes(searchTerm) || id.includes(searchTerm)) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
                }
            });
        });
    }
    
    // Create form submission
    if (createResponseForm) {
        createResponseForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const response = {
                keywords: document.getElementById('createKeywords').value,
                response: document.getElementById('createResponse').value,
                linkText: document.getElementById('createLinkText').value || null,
                linkUrl: document.getElementById('createLinkUrl').value || null
            };
            
            fetch('/chatbot/api/response', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(response),
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                document.getElementById('successMessage').textContent = 'Response added successfully!';
                new bootstrap.Modal(document.getElementById('successModal')).show();
                setTimeout(() => window.location.reload(), 1500);
            })
            .catch((error) => {
                console.error('Error:', error);
                alert('Error adding response: ' + error.message);
            });
        });
    }
    
    // Edit form submission
    if (editResponseForm) {
        editResponseForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const id = document.getElementById('editId').value;
            const response = {
                id: parseInt(id), // Convert to number
                keywords: document.getElementById('editKeywords').value,
                response: document.getElementById('editResponse').value,
                linkText: document.getElementById('editLinkText').value || null,
                linkUrl: document.getElementById('editLinkUrl').value || null
            };
            
            console.log("Updating response with ID:", id);
            
            fetch('/chatbot/api/response/' + id, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(response)
            })
            .then(response => {
                if (!response.ok) {
                    console.error("Update failed with status:", response.status);
                    return response.text().then(text => {
                        throw new Error('Server returned ' + response.status + ': ' + text);
                    });
                }
                return response.json();
            })
            .then(data => {
                document.getElementById('successMessage').textContent = 'Response updated successfully!';
                new bootstrap.Modal(document.getElementById('successModal')).show();
                setTimeout(() => window.location.reload(), 1500);
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error updating response: ' + error.message);
            });
        });
    }
    
    // Remove form submission
    if (removeResponseForm) {
        removeResponseForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const id = document.getElementById('removeId').value;
            console.log("Deleting response with ID:", id);
            
            if (confirm('Are you sure you want to delete this response?')) {
                fetch('/chatbot/api/response/' + id, {
                    method: 'DELETE',
                    headers: { 'Content-Type': 'application/json' }
                })
                .then(response => {
                    if (!response.ok) {
                        console.error("Delete failed with status:", response.status);
                        return response.text().then(text => {
                            throw new Error('Server returned ' + response.status + ': ' + text);
                        });
                    }
                    return response.text();
                })
                .then(data => {
                    document.getElementById('successMessage').textContent = 'Response removed successfully!';
                    new bootstrap.Modal(document.getElementById('successModal')).show();
                    setTimeout(() => window.location.reload(), 1500);
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Error removing response: ' + error.message);
                });
            }
        });
    }
});