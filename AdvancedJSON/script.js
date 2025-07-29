document.addEventListener('DOMContentLoaded', function () {
    // -------------------- DOM Elements --------------------
    const chatbotLauncher = document.getElementById('chatbot-launcher');
    const chatbotWindow = document.getElementById('chatbot-window');
    const minimizeBtn = document.getElementById('minimize-btn');
    const userInput = document.getElementById('user-input');
    const sendBtn = document.getElementById('send-btn');
    const messageContainer = document.getElementById('message-container');
    const tooltip = document.getElementById('tooltip');
    const micButton = document.getElementById('mic-btn');
    const speechStatus = document.getElementById('speech-status');
    const clearBtn = document.getElementById('clear-btn');

    let responses = [];
    const typingSpeed = 20;
    let firstOpen = true;

    // -------------------- Load Bot Responses --------------------
    function loadResponses() {
        const xhr = new XMLHttpRequest();
        xhr.overrideMimeType("application/json");
        xhr.open('GET', 'chatbot-responses.json', true);
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                if (xhr.status === 200 || xhr.status === 0) {
                    try {
                        responses = JSON.parse(xhr.responseText);
                    } catch {
                        responses = [];
                    }
                } else {
                    responses = [];
                }
                setTimeout(showTooltip, 1);
            }
        };
        xhr.onerror = function () {
            responses = [];
            setTimeout(showTooltip, 2000);
        };
        xhr.send(null);
    }

    loadResponses();

    function showTooltip() {
        if (!tooltip) return;
        tooltip.classList.remove('hidden');
        setTimeout(() => tooltip.classList.add('hidden'), 5000);
    }

    // -------------------- Chatbot Window Events --------------------
    chatbotLauncher?.addEventListener('click', openChatbot);
    minimizeBtn?.addEventListener('click', e => {
        e.stopPropagation();
        minimizeChatbot();
    });

    function openChatbot() {
        chatbotWindow.classList.remove('hidden');
        chatbotWindow.style.display = 'flex';
        chatbotLauncher.style.display = 'none';
        userInput?.focus();

        if (firstOpen) {
            setTimeout(() => {
                addBotMessage("Hello, I'm e-Aushadhi and e-Upkaran Assistant. How can I help you today?");
                firstOpen = false;
            }, 100);
        }
    }

    function minimizeChatbot() {
        chatbotWindow.style.display = 'none';
        chatbotLauncher.style.display = 'flex';
    }

    // -------------------- Clear Chat --------------------
    clearBtn?.addEventListener('click', () => {
        messageContainer.innerHTML = '';
        setTimeout(() => {
            addBotMessage("Hello, I'm e-Aushadhi and e-Upkaran Assistant. How can I help you today?");
        }, 100);
    });

    // -------------------- Send Message --------------------
    sendBtn?.addEventListener('click', sendMessage);
    userInput?.addEventListener('keypress', e => {
        if (e.key === 'Enter') sendMessage();
    });

    function sendMessage() {
        const message = userInput.value.trim();
        if (!message) return;
        addUserMessage(message);
        processMessage(message);
        userInput.value = '';
    }

    function processMessage(message) {
        const lowerMessage = message.toLowerCase();
        for (const item of responses) {
            if (item.keywords.some(keyword => lowerMessage.includes(keyword.toLowerCase()))) {
                setTimeout(() => addBotMessage(item.response, item.link), 10);
                return;
            }
        }
        setTimeout(() => addBotMessage("I am sorry, I can't reply to that."), 10);
    }

    // -------------------- Render Messages --------------------
    function addUserMessage(message) {
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
        scrollToBottom();
    }

    function addBotMessage(message, link) {
        const messageElement = document.createElement('div');
        messageElement.classList.add('message', 'bot-message');

        const messageText = document.createElement('div');
        messageText.classList.add('message-text');

        const typingSpan = document.createElement('span');
        typingSpan.classList.add('typing-text');
        messageText.appendChild(typingSpan);

        const timestamp = document.createElement('div');
        timestamp.classList.add('timestamp');
        timestamp.textContent = getCurrentTime();

        messageElement.appendChild(messageText);
        messageElement.appendChild(timestamp);
        messageContainer.appendChild(messageElement);
        scrollToBottom();

        let i = 0;
        function typeCharacter() {
            if (i < message.length) {
                typingSpan.textContent += message.charAt(i++);
                scrollToBottom();
                setTimeout(typeCharacter, typingSpeed);
            } else if (link) {
                setTimeout(() => {
                    const linkElement = document.createElement('div');
                    linkElement.classList.add('message-link');

                    const anchor = document.createElement('a');
                    anchor.href = link.url;
                    anchor.textContent = link.text;
                    anchor.target = "_blank";

                    linkElement.appendChild(anchor);
                    messageText.appendChild(document.createElement('br'));
                    messageText.appendChild(linkElement);
                    scrollToBottom();
                }, 500);
            }
        }

        typeCharacter();
    }

    function getCurrentTime() {
        const now = new Date();
        let hours = now.getHours();
        const minutes = now.getMinutes().toString().padStart(2, '0');
        const ampm = hours >= 12 ? 'PM' : 'AM';
        hours = hours % 12 || 12;
        return `${hours}:${minutes} ${ampm}`;
    }

    function scrollToBottom() {
        messageContainer.scrollTop = messageContainer.scrollHeight;
    }

    // -------------------- Speech Recognition --------------------
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!SpeechRecognition) {
        micButton.style.display = 'none';
    } else {
        const recognition = new SpeechRecognition();
        recognition.continuous = false;
        recognition.interimResults = true;
        recognition.lang = 'en-US';

        let isListening = false;
        let finalTranscript = '';
        let resultReceived = false;

        micButton.addEventListener('click', toggleSpeechRecognition);

        recognition.onstart = () => {
            finalTranscript = '';
            resultReceived = false;
            isListening = true;
            micButton.classList.add('listening');
            speechStatus.classList.remove('hidden');
        };

        recognition.onresult = (event) => {
            let interimTranscript = '';
            for (let i = event.resultIndex; i < event.results.length; i++) {
                const transcript = event.results[i][0].transcript;
                if (event.results[i].isFinal) {
                    finalTranscript += transcript;
                } else {
                    interimTranscript += transcript;
                }
            }

            const fullTranscript = finalTranscript || interimTranscript;
            userInput.value = fullTranscript;

            if (finalTranscript.trim()) {
                addUserMessage(finalTranscript.trim());
                processMessage(finalTranscript.trim());
                resultReceived = true;
            }
        };

        recognition.onend = () => {
            isListening = false;
            micButton.classList.remove('listening');
            speechStatus.classList.add('hidden');

            if (!resultReceived && userInput.value.trim()) {
                sendMessage();
            }
        };

        recognition.onerror = () => {
            isListening = false;
            micButton.classList.remove('listening');
            speechStatus.classList.add('hidden');
        };

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
            } catch (error) {
                console.error('Speech recognition error:', error);
            }
        }

        function stopListening() {
            recognition.stop();
        }
    }
});
