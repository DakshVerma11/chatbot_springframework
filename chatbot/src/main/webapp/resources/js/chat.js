document.addEventListener('DOMContentLoaded', function() {
    console.log("Document loaded");
    
    // DOM Elements
    const chatbotLauncher = document.getElementById('chatbot-launcher');
    const chatbotWindow = document.getElementById('chatbot-window');
    const minimizeBtn = document.getElementById('minimize-btn');
    const userInput = document.getElementById('user-input');
    const sendBtn = document.getElementById('send-btn');
    const messageContainer = document.getElementById('message-container');
    const tooltip = document.getElementById('tooltip');
    
    // Check if elements were found
    console.log("Launcher found:", chatbotLauncher !== null);
    console.log("Chatbot window found:", chatbotWindow !== null);
    
    // IMMEDIATELY hide the chatbot window and show launcher
    if (chatbotWindow) chatbotWindow.classList.add('hidden');
    if (chatbotLauncher) chatbotLauncher.classList.remove('hidden');
    
    // Typing speed (milliseconds per character)
    const typingSpeed = 20;
    
    // Show initial greeting when chatbot is first opened
    let firstOpen = true;

    // Event Listeners
    if (chatbotLauncher) {
        chatbotLauncher.addEventListener('click', function() {
            console.log("Launcher clicked");
            openChatbot();
        });
    }
    
    if (minimizeBtn) {
        minimizeBtn.addEventListener('click', function(e) {
            console.log("Minimize clicked");
            e.stopPropagation(); // Prevent event bubbling
            minimizeChatbot();
        });
    }
    
    if (sendBtn) {
        sendBtn.addEventListener('click', sendMessage);
    }
    
    if (userInput) {
        userInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                sendMessage();
            }
        });
    }

    // Show tooltip after a delay
    setTimeout(showTooltip, 1000);

    function showTooltip() {
        if (tooltip) {
            tooltip.classList.remove('hidden');
            setTimeout(() => { tooltip.classList.add('hidden'); }, 5000);
        }
    }

    // Open chatbot
    function openChatbot() {
        console.log("Opening chatbot");
        
        if (chatbotWindow && chatbotLauncher) {
            chatbotWindow.classList.remove('hidden');
            chatbotLauncher.classList.add('hidden');
            
            if (userInput) userInput.focus();
            
            if (firstOpen) {
                // Add initial greeting
                setTimeout(() => {
                    addBotMessage("Hello, I'm e-Aushadhi and e-Upkaran Assistant. How can I help you today?");
                    firstOpen = false;
                }, 100);
            }
        } else {
            console.error("Could not find chatbot elements");
        }
    }

    // Minimize chatbot
    function minimizeChatbot() {
        console.log("Minimizing chatbot");
        if (chatbotWindow && chatbotLauncher) {
            chatbotWindow.classList.add('hidden');
            chatbotLauncher.classList.remove('hidden');
        }
    }

    // Send message
    function sendMessage() {
        if (!userInput) return;
        
        const message = userInput.value.trim();
        if (message === '') return;
        
        // Add user message to chat
        addUserMessage(message);
        
        // Process message and get response from the server
        fetchBotResponse(message);
        
        // Clear input
        userInput.value = '';
    }

    // Fetch response from the server
    function fetchBotResponse(message) {
        // Show loading spinner
        showLoadingSpinner();
        
        // Make AJAX request to server
        $.ajax({
            url: "/api/chat",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({ message: message }),
            success: function(response) {
                hideLoadingSpinner();
                
                // Process the response text for any links
                let responseText = response.response;
                let link = null;
                
                // Check if the response contains a link
                if (responseText.includes("Click here for more information:")) {
                    const parts = responseText.split("Click here for more information:");
                    responseText = parts[0];
                    
                    // Extract link information
                    const linkInfo = parts[1].trim();
                    const linkMatch = linkInfo.match(/(.*?)\s*\((http.*?)\)/);
                    
                    if (linkMatch && linkMatch.length >= 3) {
                        link = {
                            text: linkMatch[1].trim(),
                            url: linkMatch[2].trim()
                        };
                    }
                }
                
                // Format newlines in the response
                responseText = responseText.replace(/\/n/g, "\n");
                
                // Add the bot's response to the chat
                addBotMessage(responseText, link);
            },
            error: function() {
                hideLoadingSpinner();
                addBotMessage("I'm sorry, I encountered an error while processing your request. Please try again.");
            }
        });
    }

    // Show loading spinner
    function showLoadingSpinner() {
        if (!messageContainer) return;
        
        const loaderElement = document.createElement('div');
        loaderElement.classList.add('message', 'bot-message', 'loading-message');
        loaderElement.id = 'loading-spinner';
        
        const spinnerImg = document.createElement('img');
        spinnerImg.src = '../static/images/triangle-spinner.gif';
        spinnerImg.alt = 'Loading...';
        spinnerImg.classList.add('spinner-image');
        
        loaderElement.appendChild(spinnerImg);
        messageContainer.appendChild(loaderElement);
        
        // Scroll to bottom
        scrollToBottom();
    }
    
    // Hide loading spinner
    function hideLoadingSpinner() {
        const spinner = document.getElementById('loading-spinner');
        if (spinner) {
            spinner.remove();
        }
    }

    // Add user message to chat
    function addUserMessage(message) {
        if (!messageContainer) return;
        
        const messageElement = document.createElement('div');
        messageElement.classList.add('message', 'user-message');
        
        const messageText = document.createElement('div');
        messageText.classList.add('message-text');
        messageText.textContent = message;
        
        const timestamp = document.createElement('div');
        timestamp.classList.add('timestamp');
        timestamp.textContent = getCurrentTime();
        
        messageElement.appendChild(messageText);
        messageElement.appendChild(timestamp);
        
        messageContainer.appendChild(messageElement);
        
        // Scroll to bottom
        scrollToBottom();
    }

    // Add bot message to chat with typing effect
    function addBotMessage(message, link) {
        if (!messageContainer) return;
        
        const messageElement = document.createElement('div');
        messageElement.classList.add('message', 'bot-message');
        
        const messageText = document.createElement('div');
        messageText.classList.add('message-text');
        
        // Create a span for the typing text
        const typingSpan = document.createElement('span');
        typingSpan.classList.add('typing-text');
        messageText.appendChild(typingSpan);
        
        const timestamp = document.createElement('div');
        timestamp.classList.add('timestamp');
        timestamp.textContent = getCurrentTime();
        
        messageElement.appendChild(messageText);
        messageElement.appendChild(timestamp);
        
        messageContainer.appendChild(messageElement);
        
        // Scroll to bottom
        scrollToBottom();
        
        // Type the message character by character
        let i = 0;
        function typeCharacter() {
            if (i < message.length) {
                typingSpan.textContent += message.charAt(i);
                i++;
                scrollToBottom();
                setTimeout(typeCharacter, typingSpeed);
            } else {
                // Add link if provided after typing is complete
                if (link) {
                    setTimeout(() => {
                        const linkElement = document.createElement('div');
                        linkElement.classList.add('message-link');
                        
                        const anchor = document.createElement('a');
                        anchor.href = link.url;
                        anchor.textContent = link.text;
                        anchor.target = "_blank"; // Open in new tab
                        
                        linkElement.appendChild(anchor);
                        messageText.appendChild(document.createElement('br'));
                        messageText.appendChild(linkElement);
                        
                        scrollToBottom();
                    }, 500); // Slight delay before showing link
                }
            }
        }
        
        // Start typing
        typeCharacter();
    }

    // Get current time in format HH:MM AM/PM
    function getCurrentTime() {
        const now = new Date();
        let hours = now.getHours();
        const minutes = now.getMinutes().toString().padStart(2, '0');
        const ampm = hours >= 12 ? 'PM' : 'AM';
        
        hours = hours % 12;
        hours = hours ? hours : 12; // Convert 0 to 12
        
        return `${hours}:${minutes} ${ampm}`;
    }

    // Scroll to bottom of messages
    function scrollToBottom() {
        if (messageContainer) {
            messageContainer.scrollTop = messageContainer.scrollHeight;
        }
    }
});