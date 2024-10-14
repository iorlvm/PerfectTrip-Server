<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<style>
    main {
        padding: 0;
    }
</style>

<div class="chat-container">
    <div id="weien-chat"></div>
</div>

<script type="module">
    import {actionHandlers, WeienChat} from '../js/weien-chat.js'

    const getChatRoomsAPI = (size, earliest) => {
        let params = [];
        if (size) params.push('size=' + size);
        if (earliest) params.push('earliest=' + earliest);

        let url = '/api/chat/rooms?' + params.join('&');
        return fetch(url, {
            method: 'GET'
        }).then(response => {
            if (!response.ok) {
                return Promise.reject(new Error(`HTTP error! Status: \${response.status}`));
            }
            return response.json();
        }).then(data => {
            return data;
        }).catch(error => {
            showAlert('取得聊天室列表失敗', 'warning');
            throw error;
        });
    };

    const getChatRoomsByChatIdAPI = (chatId) => {
        let url= '/api/chat/rooms/' + chatId;
        return fetch(url, {
            method: 'GET'
        }).then(response => {
            if (!response.ok) {
                return Promise.reject(new Error(`HTTP error! Status: \${response.status}`));
            }
            return response.json();
        }).then(data => {
            return data;
        }).catch(error => {
            showAlert('取得聊天室失敗' + chatId, 'warning');
            throw error;
        });
    }

    const getUidAPI = () => {
        return fetch('/api/chat/uid', {
            method: 'GET'
        }).then(response => {
            if (!response.ok) {
                return Promise.reject(new Error(`HTTP error! Status: \${response.status}`));
            }
            return response.json();
        }).then(data => {
            return data;
        }).catch(error => {
            showAlert('取得uid失敗', 'warning');
            throw error;
        });
    };

    const getMessagesAPI = async (chatId, messageId, size) => {
        if (!chatId) {
            showAlert('chatId不可為空', 'warning');
            throw new Error("chatId is required but was not provided.");
        }

        let params = [];
        if (messageId) params.push('messageId=' + messageId);
        if (size) params.push('size=' + size);

        let url = '/api/chat/rooms/' + chatId + '/messages?' + params.join('&')

        return fetch(url, {
            method: 'GET'
        }).then(response => {
            if (!response.ok) {
                return Promise.reject(new Error(`HTTP error! Status: \${response.status}`));
            }
            return response.json();
        }).then(data => {
            return data;
        }).catch(error => {
            showAlert('取得訊息失敗', 'warning');
            throw error;
        });
    };

    const updateChatRoomNotifyAPI = (chatId, notifySettings) => {
        if (!chatId) {
            showAlert('chatId不可為空', 'warning');
            throw new Error("chatId is required but was not provided.");
        }

        let url = '/api/chat/rooms/' + chatId + '/notify';

        return fetch(url, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({notifySettings})
        }).then(response => {
            if (!response.ok) {
                return Promise.reject(new Error(`HTTP error! Status: \${response.status}`));
            }
            return response.json();
        }).then(data => {
            return data;
        }).catch(error => {
            showAlert('修改通知設定失敗', 'warning');
            throw error;
        });
    };

    const updateChatRoomPinnedAPI = async (chatId, pinned) => {
        if (!chatId) {
            showAlert('chatId不可為空', 'warning');
            throw new Error("chatId is required but was not provided.");
        }

        let url = '/api/chat/rooms/' + chatId + '/pinned';

        return fetch(url, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({pinned})
        }).then(response => {
            if (!response.ok) {
                return Promise.reject(new Error(`HTTP error! Status: \${response.status}`));
            }
            return response.json();
        }).then(data => {
            return data;
        }).catch(error => {
            showAlert('修改釘選失敗', 'warning');
            throw error;
        });
    }

    const imageUpdateAPI = (formData) => {
        return fetch('/api/image', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json(); // 假設你期望回傳 JSON 資料
            })
            .catch(error => {
                console.error('There was a problem with the fetch operation:', error);
                throw error;
            });
    };

    let unreads = 0;

    let webSocket;

    actionHandlers.pinnedToggle = async (binder) => {
        binder.value.pinned = !binder.value.pinned
        await updateChatRoomPinnedAPI(binder.value.chatId, binder.value.pinned);
        chat.moveChatToTopOrBelowPinned(binder.value.chatId);
    }

    actionHandlers.getUID = async () => {
        const res = await getUidAPI();
        unreads = Number(res.data[1]);
        return res.data[0];
    }

    actionHandlers.getChatRoomDataByChatId = async (chatId) => {
        // TODO: 取得單間聊天室的API
        const res = await getChatRoomsByChatIdAPI(chatId);
        return res.data;
    }

    actionHandlers.updateFile = async (selectedFile) => {
        console.log(selectedFile);

        const formData = new FormData();

        formData.append('file', selectedFile);
        formData.append('cacheEnabled', false);

        const res = await imageUpdateAPI(formData);
        let message = {
            chatId: chat.getActiveChatId(),
            senderId: chat.chatUserId,
            img: {
                src: res.data
            }
        }

        if (webSocket.readyState === WebSocket.OPEN) {
            webSocket.send(JSON.stringify({
                chatId: message.chatId,
                action: 'send-message',
                content: JSON.stringify(message)
            }));
        } else {
            webSocket.addEventListener('open', () => {
                actionHandlers.sendMessage(message);
            }, { once: true });
        }
    }


    actionHandlers.getChatRoomsData = async () => {
        const res = await getChatRoomsAPI();
        return res.data;
    }

    actionHandlers.getChatMessagesData = async (chatId) => {
        const res = await getMessagesAPI(chatId);
        return res.data;
    }

    actionHandlers.loadMoreChatRooms = async (type) => {
        let lastChat = chat.getLastChat();
        const date = new Date(lastChat.value.lastModifiedAt);
        date.setTime(date.getTime() + (8 * 60 * 60 * 1000))

        // 日期格式化
        const formattedDate = date.toISOString().replace('T', ' ').substring(0, 19);
        const milliseconds = date.getMilliseconds().toString().padStart(3, '0');
        const finalOutput = `\${formattedDate}.\${milliseconds}`;

        const res = await getChatRoomsAPI(20, finalOutput);
        // TODO: 可能要檢查有沒有回傳重複的值 (萬一時間完全相同的時候可能會出現問題)
        return res.data;
    }

    actionHandlers.loadMoreMessages = async (chatId) => {
        let lastMessage = chat.getLastMessage();
        const res = await getMessagesAPI(chatId, lastMessage?.value.messageId);
        return res.data;
    }

    actionHandlers.filterPinned = async () => {
        // 前端過濾的偷懶做法 (但我的回傳設計只要是釘選聊天室的都會一次被傳到前端, 所以這個設計沒有問題)
        const chatList = chat.getChatList();
        if (chatList && chatList.length > 0) {
            let chatRoomData = chatList.map(binder => {
                return binder.value;
            })
            return chatRoomData.filter(e => e.pinned);
        } else {
            return [];
        }
    }

    actionHandlers.filterUnread = async () => {
        // 前端過濾的偷懶做法  TODO:未來應該改成去伺服器取得資料
        const chatList = chat.getChatList();
        if (chatList && chatList.length > 0) {
            let chatRoomData = chatList.map(binder => {
                return binder.value;
            })
            return chatRoomData.filter(e => e.unreadMessages > 0);
        } else {
            return [];
        }
    }

    actionHandlers.updateNotifySettings = async (chatId, state) => {
        let res = await updateChatRoomNotifyAPI(chatId, state);
        if (res.success) return state;
    }

    actionHandlers.readChatMessages = (chatId, chatUnreads) => {
        if (webSocket.readyState === WebSocket.OPEN) {
            webSocket.send(JSON.stringify({
                chatId: chatId,
                action: 'read-message'
            }));
        } else {
            webSocket.addEventListener('open', () => {
                actionHandlers.readChatMessages(chatId);
            }, {once: true});
        }
        unreads -= chatUnreads;
    }

    actionHandlers.sendMessage = (message) => {
        if (webSocket.readyState === WebSocket.OPEN) {
            webSocket.send(JSON.stringify({
                chatId: message.chatId,
                action: 'send-message',
                content: JSON.stringify(message)
            }));
        } else {
            webSocket.addEventListener('open', () => {
                actionHandlers.sendMessage(message);
            }, {once: true});
        }
    }

    const handleSendMessage = (payload) => {
        let message = JSON.parse(payload.content);
        // 更新chat room list中的資料
        chat.updateChatListInfo(payload.chatId, message);
        unreads++;

        // 接到訊息時利用authorId, chatId更新已讀時間
        chat.updateReadingAtByChatIdAndAuthorId(payload.chatId, payload.authorId);

        let activeChatId = chat.getActiveChatId();
        if (activeChatId === payload.chatId) {
            chat.appendMessage(message);
            // 重新送出一個已讀操作
            actionHandlers.readChatMessages(activeChatId);
        }
    };

    const handleReadMessage = (payload) => {
        chat.updateReadingAtByChatIdAndAuthorId(payload.chatId, payload.authorId);
    };

    const handleUpdateUserInfo = (payload) => {
    };

    const handleUpdateRoomInfo = (payload) => {
    };

    const onChatRoomConnected = e => {
    }

    const onMessageReceived = e => {
        let payload = JSON.parse(e.data);
        switch (payload.action) {
            case 'send-message':
                handleSendMessage(payload);
                break;
            case 'read-message':
                handleReadMessage(payload);
                break;
            case 'update-user-info':
                handleUpdateUserInfo(payload);
                break;
            case 'update-room-info':
                handleUpdateRoomInfo(payload);
                break;
        }
    }

    const onChatRoomClosed = e => {
    }

    const onChatRoomError = e => {
    }

    const chat = new WeienChat();
    document.addEventListener('DOMContentLoaded', async () => {
        await chat.init();
        webSocket = new WebSocket('ws://iorlvm.i234.me:8080/chat?admin');
        webSocket.addEventListener('open', onChatRoomConnected);

        webSocket.addEventListener('message', onMessageReceived);

        webSocket.addEventListener('close', onChatRoomClosed);

        webSocket.addEventListener('error', onChatRoomError);
    });
</script>