// Speech recognition functionality for the chatbot
document.addEventListener('DOMContentLoaded', function() {
    // Elements
    const micButton = document.getElementById('mic-btn');
    const userInput = document.getElementById('user-input');
    const sendButton = document.getElementById('send-btn');
    const speechStatus = document.getElementById('speech-status');
    
    // Check for browser support
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    
    if (!SpeechRecognition) {
        console.error('Speech recognition not supported in this browser');
        micButton.style.display = 'none';
        return;
    }
    
    // Initialize speech recognition
    const recognition = new SpeechRecognition();
    recognition.continuous = false;
    recognition.interimResults = false;
    recognition.lang = 'en-US';
    
    // Flag to track if we're currently listening
    let isListening = false;
    
    // Event listeners
    micButton.addEventListener('click', toggleSpeechRecognition);
    
    // Handle recognition results
    recognition.onresult = function(event) {
        const transcript = event.results[0][0].transcript;
        userInput.value = transcript;
        
        // Automatically send the message
        sendButton.click();
    };
    
    // Handle recognition end
    recognition.onend = function() {
        stopListening();
    };
    
    // Handle recognition errors
    recognition.onerror = function(event) {
        console.error('Speech recognition error:', event.error);
        stopListening();
    };
    
    // Functions to toggle speech recognition
    function toggleSpeechRecognition() {
        if (isListening) {
            stopListening();
        } else {
            startListening();
        }
    }
    
    function startListening() {
        try {
            recognition.start();
            isListening = true;
            micButton.classList.add('listening');
            speechStatus.classList.remove('hidden');
        } catch (error) {
            console.error('Error starting speech recognition:', error);
        }
    }
    
    function stopListening() {
        recognition.stop();
        isListening = false;
        micButton.classList.remove('listening');
        speechStatus.classList.add('hidden');
    }
});