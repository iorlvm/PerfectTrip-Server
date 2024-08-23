// 客製化調整區
/**
 * 設定釘選的圖標樣式
 * @returns icon標籤
 */
const pinnedIcon = () => {
    return {
        placement: 'avatar',  // 僅接受三種參數傳入 'avatar', 'text', 'both' (未設定時預設為'avatar', 輸入這三個參數以外的數值為不顯示)
        icon: '<i class="bi bi-pin-angle-fill"></i>'
    }
}

/**
 * 設定提醒開關的圖標樣式
 * @returns 物件{ on: icon標籤, off: icon標籤 }
 */
const notifIcon = () => {
    return {
        on: '<i class="bi bi-bell-fill"></i>',
        off: '<div style="transform: translateY(-1px);"><i class="bi bi-bell-slash-fill"></i></div>'
    }
}

/**
 * 設定聊天室的日期時間格式 (可依造需求進行改寫)
 * @param {string} dateStr 日期時間格式的字串
 * @returns 格式化後的值
 */
const formatChatDate = (dateStr) => {
    const date = new Date(dateStr);
    const now = new Date();

    const isToday = date.toDateString() === now.toDateString();
    const isYesterday = new Date(date.setDate(date.getDate() + 1)).toDateString() === now.toDateString();

    if (isToday) {
        return '今天';
    } else if (isYesterday) {
        return '昨天';
    } else {
        return date.toLocaleDateString('zh-TW', { month: '2-digit', day: '2-digit' });
    }
};

/**
 * 設定訊息的時間格式 (可依造需求進行改寫)
 * @param {string} dateStr 日期時間格式的字串
 * @returns 格式化後的值
 */
const formatMessagesTime = (dateStr) => {
    const date = new Date(dateStr);
    return date.toLocaleTimeString('zh-TW', { hour: '2-digit', minute: '2-digit', hour12: false })
}

/**
 * 設定聊天室的日期格式 (可依造需求進行改寫)
 * @param {string} dateStr 日期時間格式的字串
 * @returns {string} 格式化後的值
 */
const formatMessagesDate = (dateStr) => {
    const date = new Date(dateStr);
    const now = new Date();

    const isToday = date.toDateString() === now.toDateString();
    const isYesterday = new Date(date.setDate(date.getDate() + 1)).toDateString() === now.toDateString();

    if (isToday) {
        return '今天';
    } else if (isYesterday) {
        return '昨天';
    } else {
        return date.toLocaleDateString('zh-TW', { month: '2-digit', day: '2-digit' });
    }

};


/**
 * 設定聊天室下拉選單
 * icon: 下拉選單的icon 可使用額外的元素包裹並指定樣式微調
 * options: 選項陣列 輸入後會對應產出相應的選項於下拉選單中
 *     text: 選項文字
 *     click: 對應的click方法 (如需跨域執行需在actionHandlers設定對應的佔位符, 於主程式中進行函式覆寫)
 *            預設傳遞兩個參數 binder物件, 事件物件 
 *     bind: 根據值切換text的顯示內容 (成功設定時text的屬性會失效)
 *         text: [] 字串陣列, 長度為2
 *         property: 根據具體哪個property進行切換
 *         condition: 回傳值為boolean的判斷函式
 *     slider: 綁定property (根據值的響應式滑動開關, 無設定時不出現)
 */
const chatListSettings = () => {
    return {
        icon: `
            <div>
                <i class="bi bi-chevron-down"></i>
            </div>
        `,
        // 可以在這裡指定下拉欄位的寬度以及邊距, 不建議使用百分比指定寬度, 父容器的寬度非常小(約1em)
        width: 'calc(4em + 45px)',         // 未指定時的預設值: width: 5em;
        padding: '12px 10px 12px 12px',    // 未指定時的預設值: padding: '10px';
        options: [
            {
                text: '釘選',
                click: actionHandlers.pinnedToggle, // 請於下方的actionHandlers進行定義
                bind: {
                    property: 'pinned',
                    text: ['對話置頂', '取消置頂'],
                    condition: (binder) => {
                        return !binder.value.pinned;
                    }
                },
                slider: 'pinned' // 可以與bind同時使用 (如果有需要的話)
            },
            {
                // text: '釘選',  // bind設定成功時, 無傳入text也可以正常運作
                click: actionHandlers.pinnedToggle,
                bind: {
                    property: 'pinned',
                    text: [
                        // 可以直接使用行內樣式做微調 
                        `
                            <i class="bi bi-pin" style="margin-right: 8px; transform: translateY(1px); font-size:16px"></i>
                            <span>對話置頂</span>
                        `,
                        `
                            <i class="bi bi-pin-angle" style="margin-right: 8px; transform: translateY(-1px); font-size:16px"></i>
                            <span>取消置頂</span>
                        `
                    ],
                    condition: (binder) => {
                        return !binder.value.pinned;
                    }
                }
            },
            {
                text: 'log回傳值',
                click: actionHandlers.handler1
            },
            {
                text: '外部定義測試',
                click: actionHandlers.handler2
            },
        ],
        scrollEvent: {
            trigger: 'bottom',
            getData: actionHandlers.loadMoreChatRooms
        }
    }
}

/**
 * 設定聊天對象的下拉選單 (設定方式與上方相同)
 */
const chatingSettings = () => {
    return {
        icon: `
            <div style="transform: translateY(1px);">
                <i class="bi bi-three-dots-vertical"></i>
            </div>
        `,
        // 可以在這裡指定下拉欄位的寬度以及邊距, 不建議使用百分比指定寬度, 父容器的寬度非常小(約1em)
        width: 'calc(4em + 45px)',         // 未指定時的預設值: width: 5em;
        padding: '12px 10px 12px 12px',    // 未指定時的預設值: padding: '10px';
        options: [
            {
                text: '釘選',
                click: actionHandlers.pinnedToggle, // 請於下方的actionHandlers進行定義
                bind: {
                    property: 'pinned',
                    text: ['對話置頂', '取消置頂'],
                    condition: (binder) => {
                        return !binder.value.pinned;
                    }
                },
                slider: 'pinned' // 可以與bind同時使用 (如果有需要的話)
            },
            {
                // text: '釘選',  // bind設定成功時, 無傳入text也可以正常運作
                click: actionHandlers.pinnedToggle,
                bind: {
                    property: 'pinned',
                    text: [
                        // 可以直接使用行內樣式做微調 
                        `
                            <i class="bi bi-pin" style="margin-right: 8px; transform: translateY(1px); font-size:16px"></i>
                            <p>對話置頂</p>
                        `,
                        `
                            <i class="bi bi-pin-angle" style="margin-right: 8px; transform: translateY(-1px); font-size:16px"></i>
                            <p>取消置頂</p>
                        `
                    ],
                    condition: (binder) => {
                        return !binder.value.pinned;
                    }
                }
            },
            {
                text: 'log回傳值',
                click: actionHandlers.handler1
            },
            {
                text: '外部定義測試',
                click: actionHandlers.handler2
            },
        ],
        scrollEvent: {
            trigger: 'top',
            getData: actionHandlers.loadMoreMessages
        }
    }
}

/**
 * 過濾器選項設定
 */
const filterSettings = () => {
    return {
        icon: `
            <div style="margin-left: 3px;transform: translateY(1px);">
                <i class="bi bi-chevron-down"></i>
            </div>
        `,
        width: 'calc(3em + 10px)',    // 未指定時的預設值: width: 2em;
        padding: '8px 12px',          // 未指定時的預設值: padding: '10px';
        options: [
            {   // 至少要有第一個選項
                text: '全部', // 超過兩個字高機率爆版
                prefix: '<i class="bi bi-envelope"></i>',
                suffix: '',
                click: actionHandlers.getChatRoomsData
            },
            {
                text: '未讀', // 超過兩個字高機率爆版
                prefix: '<i class="bi bi-chat-dots"></i>',
                suffix: '',
                click: actionHandlers.filterUnread
            },
            {
                text: '釘選', // 超過兩個字高機率爆版
                prefix: '<i class="bi bi-pin-angle"></i>',
                suffix: '',
                click: actionHandlers.filterPinned
            },
        ]
    }
}

const textareaSettings = () => {
    return {
        icon: [
            '<i class="bi bi-send"></i>',
            '<i class="bi bi-send-fill"></i>'
        ],
        options: [
            {
                // 這是選擇檔案上傳的選項 (如果其他選項需要自己擴充)
                html: `
                    <input type="file" style="display: none;">
                    <div style="transform: scaleX(1.2) rotate(28deg);">
                        <div style="transform: scaleX(1.3)">
                            <i class="bi bi-paperclip"></i>
                        </div>
                    </div>
                `,
                click: (ele, e) => {
                    const fileInput = ele.querySelector('input');
                    if (e.target === fileInput) return; //避免click事件遞迴

                    fileInput.click();
                    fileInput.addEventListener('change', event => {
                        const selectedFile = event.target.files[0];
                        actionHandlers.updateFile(selectedFile);
                    })
                }
            },
        ]
    }
}

export const actionHandlers = {
    // 自定義選項對應方法
    pinnedToggle: (binder) => {
        binder.value.pinned = !binder.value.pinned;
    },
    handler1: (binder, e) => {
        console.log(binder);
        console.log(e.target);
    },
    handler2: () => {
        console.log('handler2預設方法');
    },
    // 需外部串接的方法區
    updateNotifySettings: async (chatId, state) => {
        console.log(`聊天室編號: ${chatId}  目標提醒設定: ${state}`);
    },
    getUID: async () => {
        console.log('請改寫這個方法, 取得登入用戶的id');
        return '';
    },
    getChatRoomsData: async () => {
        console.log(`請改寫這個方法, 回傳值為物件陣列, 格式如下: 
        [
            {
                chatId: 1,
                chatName: '聊天室名稱',     // 可不傳遞, 未設定時: 參與者兩人時顯示另一個人的名稱   大於兩人時顯示'會話群組 (人數)'
                unreadMessages: 9999,      // 大於999時, 顯示999+
                lastMessage: '最後一則訊息',
                lastMessageAt: '2024-07-05T17:55:00Z',
                photo: '/avatar1.jpg',     // 聊天室相片(只在群組對話時設定有效)  參與者人數2的時候, 只會顯示對方頭像
                participants: [
                    {
                        userId: 'user1',
                        name: 'Alice',
                        type: 'member',
                        avatar: '/avatar1.jpg',
                        lastReadingAt: '2024-06-29T12:35:00Z', // 用於計算已讀
                        // online: true,  //目前無作用  未來考慮增加
                    },
                    {
                        userId: 'user2',
                        name: 'WeiEN',
                        type: 'member',
                        avatar: '/avatar1.jpg',
                        lastReadingAt: '2024-06-29T12:05:00Z',
                        // online: true,  //目前無作用  未來考慮增加
                    },
                    // ...更多參與者
                ], // 參與者列表 可大於2個人
                notifySettings: 'on', // 'on', 'off' , 不傳遞(或非上述兩個值)則不顯示
                pinned: true,
                // _lastActivity: 'new Data()' (程式額外添加的欄位, 用於偵測更新時間)
            },
            // ...其他聊天室
        ]`);
        return [];
    },
    getChatRoomDataByChatId: async (chatId) => {
        console.log('(chatId: ' + chatId + ')');
        return {};
    },
    getChatMessagesData: async (chatId) => {
        console.log('(chatId: ' + chatId + ') => { 請改寫這個方法, 並回傳符合格式的數據陣列才能正常運作 }');
        return [];
    },
    loadMoreChatRooms: async (filterType) => {
        console.log(`聊天室列表滾到底部的事件觸發 (過濾模式: ${filterType})，如需添加更多聊天室請改寫並回傳陣列`);
        return [];
    },
    loadMoreMessages: async (chatId) => {
        console.log('訊息列表滾到頂部的事件觸發chatId: ' + chatId + '，如需加載更多訊息請改寫並回傳陣列');
        return [];
    },
    updateFile: (selectedFile) => {
        console.log('Selected file:', selectedFile);
    },
    sendMessage: (message) => {
        console.log('送出訊息: ' + message.content);
        console.log(message);
    },
    readChatMessages: (chatId, chatUnreads) => {
        console.log('聊天室id： ' + chatId);
        console.log('未讀數量： ' + chatUnreads)
    },
    filterUnread: async (e) => {
        console.log(e);
        return [];
    },
    filterPinned: async (e) => {
        console.log(e);
        return [];
    },
    filterQuery: async (query) => {
        console.log(query);
        // return [];  // 如果想做輸入篩選回傳陣列
    }
};


/**
 * 聊天室物件
 */
export class WeienChat {
    /**
     * 建構子 (未來可以新增其他屬性做參數化調整)
     * @param {string} activeChatId 初始化生效的聊天室id (如需要預設開啟某個聊天室時進行傳遞) 
     * @param {string} chatContainer 初始化html聊天容器的id (如做調整 也需要去scss檔進行相應的修改)
     */
    constructor(activeChatId = '', chatContainerId = 'weien-chat') {
        this.chatUserId = null;
        this.chatContainerId = chatContainerId;
        this._preChatId = '';
        this._resetMessage();


        this.state = new Binder({
            searchQuery: '',
            textarea: '',
            activeChatId: activeChatId,
            filter: '全部',
            showMode: 'default',  // 'default': 預設模式, 'mobile': 行動裝置模式, 'single': 限定聊天模式
        });

        this._chatSessionData = null;
        this._chatMessagesData = [];
        this._chatRoomsData = [];
        this._dropdownVisible = {
            isOpen: false,
            target: null
        };

    }

    /**
     * 初始化啟動
     */
    async init() {
        this.chatContainerElement = document.getElementById(this.chatContainerId);
        this.chatContainerElement.innerHTML = `
            <div class="chat-list-block">
                <div class="chat-list-bar">
                    <input class="chat-room-filter-input">
                    <div class="chat-room-dropdown chat-room-filter">
                        <div class="chat-room-filter-selected"></div>
                        <ul class="chat-room-dropdown-options-block"></ul>
                    </div>
                </div>
                <div class="weien-chat-scroll">
                    <div class="chat-room-list weien-chat-scroll-content"></div>
                    <div class="weien-chat-scroll-bar">
                        <div class="weien-chat-scroll-thumb"></div>
                    </div>
                </div>
            </div>
            <div class="vertical-divider"></div>
            <div class="weien-chating-block"></div>
        `;

        // 自動呼叫方法取得資料 (使用前須先於外部覆寫定義)
        this.chatUserId = await actionHandlers.getUID();
        this._setChatList(await actionHandlers.getChatRoomsData());

        this.state.bindElement('showMode', this.chatContainerElement, (el, value) => {
            // TODO 手機顯示模式 (未來擴充)
            let chatList = el.querySelector('.chat-list-block');
            let chating = el.querySelector('.weien-chating-block');
            let divider = el.querySelector('.vertical-divider');
            if (value === 'mobile' || value === 'single') {
                chatList.classList.add('chat-show-mode-mobile');
                chating.classList.add('chat-show-mode-mobile');
                divider.style.display = 'none';
                this.state.bindElement(
                    'activeChatId',
                    chatList,
                    (el, value) => {
                        if (value !== '') {
                            el.classList.add('mobile-mode-display-none');
                        } else {
                            el.classList.remove('mobile-mode-display-none');
                        }
                    }
                );
                this.state.bindElement(
                    'activeChatId',
                    chating,
                    (el, value) => {
                        if (value !== '') {
                            el.classList.remove('mobile-mode-display-none');
                        } else {
                            el.classList.add('mobile-mode-display-none');
                        }
                    }
                );
            } else {
                divider.style.display = 'block';
                chatList.classList.remove('chat-show-mode-mobile');
                chating.classList.remove('chat-show-mode-mobile');
            }
        });

        this._renderListBar();
        this._renderChatRooms();
        this._renderChatingBlock();
    }

    /**
     * 取得聊天室列表的響應式資料陣列
     * @returns 聊天室列表陣列資料
     */
    getChatList() {
        return this._chatRoomsData;
    }

    getLastChat() {
        return this._chatRoomsData[this._chatRoomsData.length - 1];
    }

    getLastMessage() {
        return this._preMessage;
    }

    getActiveChatId() {
        return this.state.value.activeChatId;
    }

    /**
     * 將聊天室新增到聊天列表尾端
     * @param {object} chatRoom
     */
    appendChat(chatRoom) {
        if (typeof chatRoom !== 'object' || chatRoom === null) return;
        let listContainer = this.chatContainerElement.querySelector('.chat-room-list');

        chatRoom._lastActivity = new Date(); // 新增資料時間戳
        let chat = new Binder(chatRoom);
        this._chatRoomsData.push(chat); // 加進聊天室陣列資料中

        let chatCard = this._createChatCard(chat);
        listContainer.append(chatCard);
        this._resizeScrollbar(listContainer);
    }

    /**
    * 將聊天室新增到聊天列表頂端
    * @param {object} chatRoom
    */
    prependChat(chatRoom) {
        if (typeof chatRoom !== 'object' || chatRoom === null) return;
        let listContainer = this.chatContainerElement.querySelector('.chat-room-list');

        chatRoom._lastActivity = new Date(); // 新增資料時間戳
        let chat = new Binder(chatRoom);
        let chatCard = this._createChatCard(chat);

        if (chat.value.pinned) {
            this._chatRoomsData.unshift(chat); // 加進聊天室陣列資料中
            listContainer.prepend(chatCard);
        } else {
            // 修改成置頂之下的第一筆
            const lastPinnedIndex = this._chatRoomsData.findIndex(item => !item.value.pinned);

            if (lastPinnedIndex === -1) {
                this._chatRoomsData.push(chat);
                listContainer.append(chatCard);
            } else {
                this._chatRoomsData.splice(lastPinnedIndex, 0, chat);
                listContainer.insertBefore(chatCard, listContainer.children[lastPinnedIndex]);
            }
        }
        this._resizeScrollbar(listContainer);
    }

    /**
     * 將訊息新增到訊息列表尾端
     * @param {object} message
     */
    appendMessage(messageData) {
        if (typeof messageData !== 'object' || messageData === null) return;
        let listContainer = this.chatContainerElement.querySelector('.weien-messages-list');

        let message = new Binder(messageData);
        this._chatMessagesData.unshift(message); // 加進訊息陣列中

        let messageCard = this._createMessageCard(message);

        if (this._lastMessageCard && this._lastMessage.value.senderId !== message.value.senderId) {
            messageCard.classList.add('weien-header-message');
        }

        if (this._lastMessageCard && !this._isSameDay(message.value.timestamp, this._lastMessage.value.timestamp)) {
            let dateTag = document.createElement('div');
            dateTag.classList.add('weien-message-date-tag');
            dateTag.innerHTML = `<span>${formatMessagesDate(message.value.timestamp)}</span>`;
            listContainer.append(dateTag);
        }

        this._lastMessage = message;
        this._lastMessageCard = messageCard;

        listContainer.append(messageCard);
        this._resizeScrollbar(listContainer);
        this._scrollToBottom(listContainer.closest('.weien-chat-scroll'));
    }

    /**
     * 將訊息新增到訊息列表頂端
     * @param {object} message
     */
    prependMessage(messageData) {
        if (typeof messageData !== 'object' || messageData === null) return;
        let listContainer = this.chatContainerElement.querySelector('.weien-messages-list');

        let message = new Binder(messageData);
        this._chatMessagesData.push(message); // 加進訊息陣列中

        let messageCard = this._createMessageCard(message);

        // 連續訊息的頭部判斷  如果最頂部的訊息與新加載的訊息是同一個發送人  就不顯示之前的頭部標籤
        if (this._preMessageCard && this._preMessage.value.senderId === message.value.senderId) {
            this._preMessageCard.classList.remove('weien-header-message');
        }

        // 最後加進去的訊息肯定是頭部
        messageCard.classList.add('weien-header-message');

        // 如果最上方的子元素是日期標籤 就先移除掉
        if (listContainer.children.length > 0) {
            const firstChild = listContainer.children[0];
            if (firstChild.classList.contains('weien-message-date-tag')) {
                listContainer.removeChild(firstChild);
            }
        }

        // 判斷新加載的訊息是否跨日 (跨日就新增一個日期標籤)
        if (this._preMessage && !this._isSameDay(message.value.timestamp, this._preMessage.value.timestamp)) {
            let dateTag = document.createElement('div');
            dateTag.classList.add('weien-message-date-tag');
            dateTag.innerHTML = `<span>${formatMessagesDate(this._preMessage.value.timestamp)}</span>`;
            listContainer.prepend(dateTag);
        }


        this._preMessage = message;
        this._preMessageCard = messageCard;

        listContainer.prepend(messageCard);
        this._resizeScrollbar(listContainer);
    }

    async updateChatListInfo(chatId, message) {
        let flag = false;
        this._chatRoomsData.forEach(chat => {
            if (chat.value.chatId === chatId) {
                flag = true;
                if (this.state.value.activeChatId === chatId) {
                    chat.value.unreadMessages = 0;
                } else {
                    chat.value.unreadMessages++;
                }
                chat.value.lastMessage = message.content ? message.content : '圖片';
                chat.value.lastMessageAt = message.timestamp;
                this.moveChatToTopOrBelowPinned(chatId);
                return;
            }
        });
        if (flag) return;
        // 代表這筆聊天室資料尚未被讀取到前端中
        let chatRoom = await actionHandlers.getChatRoomDataByChatId(chatId);
        this.prependChat(chatRoom);
    }

    updateReadingAtByChatIdAndAuthorId(chatId, authorId) {
        this._chatRoomsData.forEach(chat => {
            if (chat.value.chatId === chatId) {
                chat.value.participants.forEach(participant => {
                    if (participant.userId === authorId) {
                        participant.lastReadingAt = new Date().toISOString();

                    }
                });
                chat.value._lastActivity = new Date();
            }
        });
    }

    moveChatToTopOrBelowPinned(chatId) {
        let listContainer = this.chatContainerElement.querySelector('.chat-room-list');
        const chatIndex = this._chatRoomsData.findIndex(chat => chat.value.chatId === chatId);

        if (chatIndex === -1) {
            // 老實說不可能發生
            console.error('Chat not found');
            return;
        }

        // 先將原始資料從陣列中移除
        const [chat] = this._chatRoomsData.splice(chatIndex, 1);

        if (chat.value.pinned) {
            // 如果釘選中就直接放到頂部
            this._chatRoomsData.unshift(chat);
            listContainer.prepend(document.querySelector(`#chat-card-${chatId}`));
        } else {
            // 非釘選狀態, 先找到第一個非釘選的位置
            const lastPinnedIndex = this._chatRoomsData.findIndex(item => !item.value.pinned);

            if (lastPinnedIndex === -1) {
                // 找不到非釘選的聊天室, 直接加到底部
                this._chatRoomsData.push(chat);
                listContainer.append(document.querySelector(`#chat-card-${chatId}`));
            } else {
                // 將資料放到最後一個釘選的聊天室之後
                this._chatRoomsData.splice(lastPinnedIndex, 0, chat);
                listContainer.insertBefore(document.querySelector(`#chat-card-${chatId}`), listContainer.children[lastPinnedIndex]);
            }
        }
    }


    // 初始化訊息紀錄 (渲染用)
    _resetMessage() {
        this._moreMessages = true;
        this._preMessage = null;
        this._preMessageCard = null;
        this._lastMessage = null;
        this._lastMessageCard = null;
    }

    _renderListBar() {
        let listBar = this.chatContainerElement.querySelector('.chat-list-bar');
        let selected = listBar.querySelector('.chat-room-filter-selected');
        let optionBlock = listBar.querySelector('.chat-room-dropdown-options-block');
        let seaechInput = listBar.querySelector('.chat-room-filter-input');

        this.state.bindElement(
            'searchQuery',
            seaechInput
        )

        this.state.bindElement(
            'searchQuery',
            seaechInput,
            (el, value) => {
                if (value.trim().length > 0) {
                    el.classList.add('filter-input-focus');
                } else {
                    el.classList.remove('filter-input-focus');
                }
            }
        )

        this.state.bindElement(
            'searchQuery',
            listBar.querySelector('.chat-room-filter'),
            (el, value) => {
                if (value.trim().length > 0) {
                    el.classList.add('filter-input-focus');
                } else {
                    el.classList.remove('filter-input-focus');
                }
            }
        )

        let settings = filterSettings();

        let isChange = false; // 避免重複請求
        seaechInput.addEventListener('input', async () => {
            if (this.state.value.searchQuery.trim().length > 0) {
                let filteredData = await actionHandlers.filterQuery(this.state.value.searchQuery.trim());
                if (filteredData) {
                    isChange = true;
                    this._setChatList(filteredData);
                    this._renderChatRooms();
                }
            } else {
                if (!isChange) return;
                let defaultOption = settings.options[0];
                this.state.value.filter = defaultOption.text;
                let arrayData = await defaultOption.click();
                this._setChatList(arrayData);
                this._renderChatRooms();
                isChange = false;
            }
        })

        this.state.bindElement(
            'filter',
            selected,
            (el, value) => {
                el.innerHTML = `<div>${value}</div>${settings.icon}`;
            }
        )
        selected.addEventListener('click', e => {
            e.stopPropagation();
            optionBlock.classList.toggle('dropdown-display-block');
        });
        document.addEventListener('click', () => {
            optionBlock.classList.remove('dropdown-display-block');
        });

        settings.options.forEach(option => {
            let optionEle = document.createElement('li');
            optionEle.classList.add('chat-room-dropdown-option');
            optionEle.innerHTML = option.prefix + option.text + option.suffix;
            optionEle.addEventListener('click', async e => {
                this.state.value.filter = option.text;
                let filteredData = await option.click(e);
                if (filteredData) {
                    this._setChatList(filteredData);
                    this._renderChatRooms();
                }
            })
            optionBlock.append(optionEle);
        });

        if (settings.width) {
            this.chatContainerElement.style.setProperty('--weien-chat-room-card-dropdown-filter-option-size', settings.width);
        }

        if (settings.padding) {
            this.chatContainerElement.style.setProperty('--weien-chat-room-card-dropdown-filter-option-padding', settings.padding);
        }
    }

    _getChatSessionData(chatId) {
        let res;
        this._chatRoomsData.forEach(chatRoom => {
            if (chatRoom.value.chatId === chatId) {
                res = chatRoom;
                return;
            }
        });
        if (res.value.unreadMessages > 0) {
            actionHandlers.readChatMessages(chatId, res.value.unreadMessages);
            res.value.unreadMessages = 0;
        }
        return res;
    }

    async _renderChatingBlock() {
        let chatingBlock = this.chatContainerElement.querySelector('.weien-chating-block');
        this.state.bindElement(
            'activeChatId',
            chatingBlock,
            async (el, value) => {
                if (value === this._preChatId) return;
                this._preChatId = value;
                this._resetMessage();

                if (value === '') {
                    el.innerHTML = '';
                } else {
                    // 取得聊天資料
                    this._chatSessionData = this._getChatSessionData(value);

                    // 取得聊天訊息列表
                    this._chatMessagesData = await this._setChatMessages();
                    el.innerHTML = `
                        <div class="weien-chating-header">
                            <div class="weien-chating-name-block">
                                <div class="weien-chating-name"></div>
                                <div class="weien-chating-participants"></div>
                            </div>
                            <div class="weien-notif-icon"></div>
                            <div style="flex:1"></div>
                            <div class="weien-chating-state-tags"></div>
                            <div class="weien-chating-dropdown"></div>
                        </div>
                        <div class="weien-chat-scroll">
                            <div class="weien-messages-list weien-chat-scroll-content">1234</div>
                            <div class="weien-chat-scroll-bar">
                                <div class="weien-chat-scroll-thumb"></div>
                            </div>
                        </div>
                        <div class="weien-chating-input-block">
                            <textarea class="weien-message-input" maxlength="500" placeholder="請輸入您的訊息"></textarea>
                            <div class="weien-chating-input-bar">
                                <div class="weien-input-options"></div>
                                <div class="weien-input-send-message"></div>
                            </div>
                        </div>
                    `;
                    this._renderChatingHeader(el.querySelector('.weien-chating-header'));
                    this._renderMessagesList(el.querySelector('.weien-messages-list'));
                    this._renderScrollbar(el.querySelectorAll('.weien-chat-scroll'), true, 'message', chatingSettings().scrollEvent);
                    this._addInputBlockEventListener();
                }
            }
        )
    }

    _renderChatingHeader(chatingHeader) {
        let chatingName = chatingHeader.querySelector('.weien-chating-name');
        this._chatSessionData.bindElement(
            'chatName',
            chatingName,
            (el, value) => {
                let len = this._chatSessionData.value.participants.length;
                let text;
                if (value && value !== '') {
                    text = value;
                } else if (len > 2) {
                    text = '會話群組';
                } else if (len === 2) {
                    text = this._getChatingWith(this._chatSessionData).name;
                } else {
                    text = 'ERROR - 這個聊天室只有一個參與者';
                }
                el.textContent = text;
            }
        );

        let participants = chatingHeader.querySelector('.weien-chating-participants');
        this._chatSessionData.bindElement(
            'participants',
            participants,
            (el, value) => {
                if (value.length > 2) {
                    el.style.display = 'block';
                    el.textContent = `(${value.length})`;
                } else {
                    el.style.display = 'none';
                    el.textContent = '';
                }
            }
        );
        let nameBlock = chatingHeader.querySelector('.weien-chating-name-block');
        nameBlock.addEventListener('click', () => {
            // TODO: 新增點擊後顯示名稱詳細訊息畫面
        })

        this._chatSessionData.bindElement(
            'participants',
            nameBlock, // 需要綁定的元素還沒創 之後記得改
            (el, value) => {
                // TODO: 根據參與者數量修改點擊名稱後顯示的畫面
                if (value.length > 2) {
                    // 顯示參與者列表
                } else {
                    // 顯示對方的詳細訊息
                }
            }
        );

        let notif = chatingHeader.querySelector('.weien-notif-icon');
        this._chatSessionData.bindElement(
            'notifySettings',
            notif,
            (el, value) => {
                if (value === 'on') {
                    el.innerHTML = notifIcon().on;
                } else if (value === 'off') {
                    el.innerHTML = notifIcon().off;
                } else {
                    el.style = "display: none;";
                }
            }
        );

        notif.addEventListener('click', async () => {
            let state = this._chatSessionData.value.notifySettings;

            if (state === 'on') {
                this._chatSessionData.value.notifySettings = 'off';
            } else if (state === 'off') {
                this._chatSessionData.value.notifySettings = 'on';
            } else {
                this._chatSessionData.value.notifySettings = 'none';
            }

            state = await actionHandlers.updateNotifySettings(this._chatSessionData.value.chatId, this._chatSessionData.value.notifySettings);
            if (state) this._chatSessionData.value.notifySettings = state;
        })

        let dropdownBlock = chatingHeader.querySelector('.weien-chating-dropdown');
        let dropdown = this._createDropdown(this._chatSessionData, chatingSettings, false);
        dropdownBlock.append(dropdown);
    }

    _setChatList(chatRoomsArray) {
        this._chatRoomsData = chatRoomsArray.map(item => {
            item._lastActivity = new Date(); // 設定資料的時間 作為更新憑據
            if (typeof item === 'object' && item !== null) {
                return new Binder(item);
            } else {
                return item;
            }
        });
    }

    async _setChatMessages() {
        let messagesArray = await actionHandlers.getChatMessagesData(this._chatSessionData.value.chatId);
        if (messagesArray) {
            return messagesArray.map(item => {
                if (typeof item === 'object' && item !== null) {
                    return new Binder(item);
                } else {
                    return item;
                }
            });
        } else {
            this._moreMessages = false;
            return [];
        }
    }

    _getChatingWith(binder = this._chatSessionData) {
        let res;
        binder.value.participants.forEach(participant => {
            if (this.chatUserId !== participant.userId) {
                res = participant;
                return;
            }
        });
        return res;
    }

    _createMessageCard(message) {
        let messageCard = document.createElement('div');
        messageCard.classList.add('weien-message-card');
        let sender = this._getSenderById(message.value.senderId);

        let content = '';
        if (message.value.img) {
            content = `<img src="${message.value.img.src}" alt="${message.value.img.alt}" style="margin-top: 3px"></img>`;
        }
        content += `<p style="margin-top: 3px">${message.value.content}</p>`;
        messageCard.innerHTML = `
            <div class="chat-room-avatar">
                <div class="weien-message-sender-name">${sender.name}</div>
            </div>
            <div class="weien-message">${content}</div>
            <div class="weien-message-state">
                <p class="weien-message-state-read"></p>
                <p class="weien-message-state-time">${formatMessagesTime(message.value.timestamp)}</p>
            </div>
        `;

        messageCard.querySelector('.chat-room-avatar').style.backgroundImage = `url(${sender.avatar})`
        // 已讀數量
        if (message.value.senderId === this.chatUserId) {
            messageCard.classList.add('weien-message-right');
            let haveRead = messageCard.querySelector('.weien-message-state-read');
            this._chatSessionData.bindElement(
                '_lastActivity',
                haveRead,
                (el) => {
                    let count = 0;
                    let messageTime = new Date(message.value.timestamp);
                    this._chatSessionData.value.participants.forEach(participant => {
                        if (participant.userId !== this.chatUserId) {
                            let lastRead = new Date(participant.lastReadingAt);
                            if (lastRead >= messageTime) count++;
                        }
                    })
                    if (count > 0) {
                        el.textContent = `已讀 ${count > 1 ? count : ''}`;
                    }
                }
            )
        } else {
            messageCard.classList.add('weien-message-left');
        }
        return messageCard;
    }

    _isSameDay(dateStr1, dateStr2) {
        const date1 = new Date(dateStr1);
        const date2 = new Date(dateStr2);

        return date1.getFullYear() === date2.getFullYear() &&
            date1.getMonth() === date2.getMonth() &&
            date1.getDate() === date2.getDate();
    }

    _renderMessagesList(messagesList) {
        messagesList.innerHTML = ''; // 確保清空
        this._chatMessagesData?.forEach(message => {
            let messageCard = this._createMessageCard(message);

            if (this._preMessageCard && this._preMessage.value.senderId !== message.value.senderId) {
                this._preMessageCard.classList.add('weien-header-message');
            }

            // TODO 日期標籤
            if (this._preMessage && !this._isSameDay(message.value.timestamp, this._preMessage.value.timestamp)) {
                let dateTag = document.createElement('div');
                dateTag.classList.add('weien-message-date-tag');
                dateTag.innerHTML = `<span>${formatMessagesDate(this._preMessage.value.timestamp)}</span>`;
                messagesList.prepend(dateTag);
            }


            if (this._lastMessage === null) {
                this._lastMessage = message;
            }

            if (this._lastMessageCard === null) {
                this._lastMessageCard = messageCard;
            }

            this._preMessage = message;
            this._preMessageCard = messageCard;
            messagesList.prepend(messageCard);
        });
        if (this._preMessageCard) this._preMessageCard.classList.add('weien-header-message');

        // 最頂部的日期標籤 (當訊息日期不是今天的時候才會顯)
        if (this._preMessage && !this._isSameDay(new Date(), this._preMessage.value.timestamp)) {
            let dateTag = document.createElement('div');
            dateTag.classList.add('weien-message-date-tag');
            dateTag.innerHTML = `<span>${formatMessagesDate(this._preMessage.value.timestamp)}</span>`;
            messagesList.prepend(dateTag);
        }
    }

    _getSenderById(senderId) {
        let res;
        this._chatSessionData.value.participants.forEach(participant => {
            if (participant.userId === senderId) {
                res = participant
                return;
            }
        });
        return res;
    }

    _addInputBlockEventListener() {
        const textarea = this.chatContainerElement.querySelector('.weien-message-input');
        const messageIcon = this.chatContainerElement.querySelector('.weien-input-send-message');
        const optionsBlock = this.chatContainerElement.querySelector('.weien-input-options');
        this.state.bindElement(
            'textarea',
            textarea,
            (el, value) => {
                el.value = value;
            }
        )

        this.state.bindElement(
            'textarea',
            messageIcon,
            (el, value) => {
                if (value.trim().length > 0) {
                    el.innerHTML = textareaSettings().icon[1];
                    el.classList.add('weien-input-send-message-active');
                } else {
                    el.innerHTML = textareaSettings().icon[0];
                    el.classList.remove('weien-input-send-message-active');
                }
            }
        )

        textarea.addEventListener('keydown', (event) => {
            if (event.key === 'Enter') {
                if (event.shiftKey) {
                    return;
                } else {
                    event.preventDefault();
                    let message = this.state.value.textarea;
                    if (message.trim().length < 1) return;

                    actionHandlers.sendMessage({
                        chatId: this._chatSessionData.value.chatId,
                        senderId: this.chatUserId,
                        content: message,
                    });
                    this.state.value.textarea = '';
                }
            }
        });

        messageIcon.addEventListener('click', (event) => {
            event.preventDefault();
            let message = this.state.value.textarea;
            if (message.trim().length < 1) return;

            actionHandlers.sendMessage({
                chatId: this._chatSessionData.value.chatId,
                senderId: this.chatUserId,
                content: message,
            });
            this.state.value.textarea = '';
        });

        textareaSettings().options.forEach(option => {
            const optionDiv = document.createElement('div');
            optionDiv.classList.add('weien-input-option');
            optionDiv.innerHTML = option.html;
            optionDiv.addEventListener('click', e => {
                option.click(optionDiv, e);
            })
            optionsBlock.append(optionDiv);
        });
    }

    _resizeScrollbar(element) {
        const scrollBar = element.closest('.weien-chat-scroll');
        const content = scrollBar.querySelector('.weien-chat-scroll-content');
        const scrollThumb = scrollBar.querySelector('.weien-chat-scroll-thumb');
        const contentHeight = content.scrollHeight;
        const containerHeight = scrollBar.clientHeight;
        if (contentHeight <= containerHeight) {
            scrollThumb.style.height = 0;
        } else {
            const thumbHeight = containerHeight / contentHeight * containerHeight;
            scrollThumb.style.height = `${thumbHeight - 3}px`; // 避免底部的1px圓角被吃掉
        }
    }

    _scrollToBottom(container) {
        const content = container.querySelector('.weien-chat-scroll-content');
        const containerHeight = container.clientHeight;
        const contentHeight = content.scrollHeight;
        content.scrollTop = contentHeight - containerHeight;
    }

    _renderScrollbar(scrollbars, bottom = false, target, settings) {
        // 監聽滾動條父元素的大小變化 動態更新scroll-bar大小
        const minThumbHeight = 13; // 設定最小的thumb高度
        const resizeObserver = new ResizeObserver(entries => {
            for (let entry of entries) {
                let parent = entry.target;
                const parentHeight = parent.clientHeight;

                // 統計scroll容器以外, 所有子元素的高度
                let totalHeight = 0;
                Array.from(parent.children).forEach(child => {
                    if (!child.classList.contains('weien-chat-scroll')) {
                        totalHeight += child.offsetHeight;
                    }
                });
                const container = parent.querySelector('.weien-chat-scroll');
                const content = container.querySelector('.weien-chat-scroll-content');
                const scrollThumb = container.querySelector('.weien-chat-scroll-thumb');

                // 計算剩餘空間, 設定scroll容器大小
                container.style.height = `${parentHeight - totalHeight}px`;

                // 設定thumb的大小
                const contentHeight = content.scrollHeight;
                const containerHeight = container.clientHeight;
                if (contentHeight <= containerHeight) {
                    scrollThumb.style.minHeight = 0;
                    scrollThumb.style.height = 0;
                } else {
                    let thumbHeight = containerHeight / contentHeight * containerHeight;
                    scrollThumb.style.minHeight = '10px';

                    // 設定最小的thumb高度
                    if (thumbHeight < minThumbHeight) {
                        thumbHeight = minThumbHeight;
                    }

                    scrollThumb.style.height = `${thumbHeight - 3}px`; // 避免底部的1px圓角被吃掉
                }
                if (bottom) {
                    this._scrollToBottom(container);
                }
            }
        });

        scrollbars.forEach(container => {
            const parent = container.parentElement;
            const content = container.querySelector('.weien-chat-scroll-content');
            const scrollBar = container.querySelector('.weien-chat-scroll-bar');
            const scrollThumb = container.querySelector('.weien-chat-scroll-thumb');
            resizeObserver.observe(parent);

            let timeout;
            const onScroll = async () => {
                const contentHeight = content.scrollHeight;
                const containerHeight = container.clientHeight;
                const scrollTop = content.scrollTop;

                let thumbTop = scrollTop / contentHeight * containerHeight;
                // 根據內容的高度和滾動位置計算thumb的位置
                if (scrollThumb.clientHeight <= minThumbHeight - 3) {
                    let divisor = contentHeight / (containerHeight - scrollThumb.clientHeight);
                    if (divisor < 55) divisor /= 1.035; // 參數微調 
                    thumbTop = scrollTop / divisor;
                    if (thumbTop >= containerHeight - minThumbHeight) {
                        thumbTop = containerHeight - minThumbHeight;
                    }
                }

                scrollThumb.style.top = `${thumbTop + 1}px`; // 參數微調, 不希望完全觸頂
                scrollBar.classList.add('on-scroll');

                if (timeout) {
                    clearTimeout(timeout);
                }

                timeout = setTimeout(() => {
                    scrollBar.classList.remove('on-scroll');
                }, 1500);

                if (settings) {
                    if (target === 'message') {
                        if (!this._moreMessages) return;

                        if (settings.trigger === 'bottom' && scrollTop + containerHeight >= contentHeight) {
                            let dataArray = await settings.getData(this.state.value.activeChatId);
                            if (!dataArray || dataArray.length === 0) {
                                this._moreMessages = false;
                                return;
                            }

                            dataArray.forEach(message => {
                                this.appendMessage(message);
                            });
                        } else if (settings.trigger === 'top' && scrollTop === 0) {
                            let dataArray = await settings.getData(this.state.value.activeChatId);
                            if (!dataArray || dataArray.length === 0) {
                                this._moreMessages = false;
                                return;
                            }

                            let preContentHeight = contentHeight;
                            dataArray.forEach(message => {
                                this.prependMessage(message);
                            });
                            content.scrollTop = content.scrollHeight - preContentHeight;
                        }
                    } else if (target === 'chat') {
                        if (settings.trigger === 'bottom' && scrollTop + containerHeight >= contentHeight) {
                            let dataArray = await settings.getData(this.state.value.filter);
                            if (!dataArray) {
                                return;
                            }

                            dataArray.forEach(chat => {
                                this.appendChat(chat);
                            });
                        } else if (settings.trigger === 'top' && scrollTop === 0) {
                            let dataArray = await settings.getData(this.state.value.filter);
                            if (!dataArray) {
                                return;
                            }

                            let preContentHeight = contentHeight;
                            dataArray.forEach(chat => {
                                this.prependChat(chat);
                            });
                            content.scrollTop = content.scrollHeight - preContentHeight;
                        }
                    }
                }
            }

            const onThumbMouseDown = (event) => {
                event.preventDefault();
                const startY = event.clientY;
                const startTop = parseInt(scrollThumb.style.top, 10) || 0;

                const onMouseMove = (event) => {
                    const deltaY = event.clientY - startY;
                    const newTop = startTop + deltaY;
                    const containerHeight = container.clientHeight;
                    const contentHeight = content.scrollHeight;
                    const maxTop = containerHeight - scrollThumb.clientHeight;

                    if (newTop < 0) {
                        scrollThumb.style.top = '0px';
                    } else if (newTop > maxTop) {
                        scrollThumb.style.top = `${maxTop - 2}px`; // 參數微調, 不希望完全觸底
                    } else {
                        scrollThumb.style.top = `${newTop}px`;
                    }

                    // 根據thumbHeight是否達到最小高度來計算scrollTop
                    if (scrollThumb.clientHeight <= minThumbHeight) {
                        content.scrollTop = parseInt(scrollThumb.style.top, 10) / (containerHeight - minThumbHeight) * contentHeight;
                    } else {
                        content.scrollTop = parseInt(scrollThumb.style.top, 10) / containerHeight * contentHeight;
                    }
                }

                const onMouseUp = () => {
                    document.removeEventListener('mousemove', onMouseMove);
                    document.removeEventListener('mouseup', onMouseUp);
                    timeout = setTimeout(() => {
                        scrollBar.classList.remove('on-scroll');
                    }, 180);
                }

                document.addEventListener('mousemove', onMouseMove);
                document.addEventListener('mouseup', onMouseUp);
            }

            scrollThumb.addEventListener('mousedown', onThumbMouseDown);
            content.addEventListener('scroll', onScroll);
        })
    }

    _renderChatRooms() {
        let listContainer = this.chatContainerElement.querySelector('.chat-room-list');
        listContainer.innerHTML = ''; // 確保清空

        this._chatRoomsData.forEach(chat => {
            let chatCard = this._createChatCard(chat);
            listContainer.append(chatCard);
        });
        this._renderScrollbar(this.chatContainerElement.querySelectorAll('.weien-chat-scroll'), false, 'chat', chatListSettings().scrollEvent);
    }

    _createChatCard(chat) {
        let chatCard = document.createElement('div');
        chatCard.id = `chat-card-${chat.value.chatId}`
        chatCard.classList.add('chat-room-card');
        chatCard.addEventListener('click', () => {
            this.state.value.activeChatId = chat.value.chatId;
        });
        this.state.bindElement(
            'activeChatId',
            chatCard,
            (el, value) => {
                if (value === chat.value.chatId) {
                    el.classList.add('active-chat');
                } else {
                    el.classList.remove('active-chat');
                }
            }
        );

        chatCard.innerHTML = `
            <div class="chat-room-avatar">
                <div class="chat-room-avatar-pinned"></div>
            </div>
            <div class="chat-room-overview">
                <div class="chat-room-header">
                    <div style="display: flex; align-items: center;">
                        <div class="chat-room-name"></div>
                        <div class="chat-room-participants"></div>
                    </div>
                    <div class="chat-room-date"></div>
                </div>
                <div class="chat-messages-preview">
                    <p class="chat-room-details"></p>
                    <div class="chat-room-icon-block"></div>
                </div>
            </div>
        `

        let chatAvatar = chatCard.querySelector('.chat-room-avatar');

        chat.bindElement(
            'photo',
            chatAvatar,
            (el, value) => {
                el.style.backgroundImage = `url(${value})`;
            }
        );

        let len = chat.value.participants.length;
        let chatingWith;
        if (len === 2) {
            chatingWith = this._getChatingWith(chat);
            chat.value.photo = chatingWith.avatar;
        }

        let chatName = chatCard.querySelector('.chat-room-name');
        chat.bindElement(
            'chatName',
            chatName,
            (el, value) => {
                let text;
                if (value && value !== '') {
                    text = value;
                } else if (len > 2) {
                    text = '會話群組';
                } else if (len === 2) {
                    text = chatingWith.name;
                } else {
                    text = 'ERROR - 這個聊天室只有一個參與者';
                }
                el.textContent = text;
            }
        );
        let participants = chatCard.querySelector('.chat-room-participants');
        chat.bindElement(
            'participants',
            participants,
            (el, value) => {
                if (value.length > 2) {
                    el.style.display = 'block';
                    el.textContent = `(${value.length})`;
                } else {
                    el.style.display = 'none';
                    el.textContent = '';
                }
            }
        )

        let chatDate = chatCard.querySelector('.chat-room-date');
        chat.bindElement(
            'lastMessageAt',
            chatDate,
            (el, value) => {
                el.textContent = formatChatDate(value);
            }
        );

        let MessagePreview = chatCard.querySelector('.chat-room-details');
        chat.bindElement(
            'lastMessage',
            MessagePreview
        )

        let iconBlock = chatCard.querySelector('.chat-room-icon-block');

        let placement = pinnedIcon().placement;
        if (placement == null || placement == 'avatar' || placement == 'both') {
            // 需顯示到頭像下方
            let pinned = chatAvatar.querySelector('.chat-room-avatar-pinned');
            chat.bindElement(
                'pinned',
                pinned,
                (el, value) => {
                    if (value) {
                        el.innerHTML = '<div = class="pinned-icon">' + pinnedIcon().icon + '</div>'
                    } else {
                        el.innerHTML = '';
                    }

                }
            );
        }

        if (placement != null && (placement == 'text' || placement == 'both')) {
            // 需顯示到文字框
            let pinned = document.createElement('div');
            chat.bindElement(
                'pinned',
                pinned,
                (el, value) => {
                    if (value) {
                        el.innerHTML = '<div = class="pinned-icon">' + pinnedIcon().icon + '</div>';
                    } else {
                        el.innerHTML = '';
                    }

                }
            );
            iconBlock.append(pinned);
        }


        let unread = document.createElement('div');
        chat.bindElement(
            'unreadMessages',
            unread,
            (el, value) => {
                if (value) {
                    el.innerHTML = `<div = class="unread-count"><span>${value < 1000 ? value : '999+'}</span></div>`;
                } else {
                    el.innerHTML = '';
                }
            }
        );
        iconBlock.append(unread);


        let dropdown = this._createDropdown(chat, chatListSettings, true);
        iconBlock.append(dropdown);

        return chatCard;
    }

    _validateOptionsBind(bind, binder) {
        // 檢查 text 是否為陣列且長度為2
        if (!Array.isArray(bind.text) || bind.text.length !== 2) {
            console.log('bind.text格式錯誤, 需為陣列且長度為2');
            return false;
        }

        // 檢查 property 是否為字串且 options.bind[property] 確實存在
        if (typeof bind.property !== 'string' || !(bind.property in binder.value)) {
            console.log('property需為一個字串且在binder中存在');
            return false;
        }

        // 檢查 condition 是否為函數
        if (typeof bind.condition !== 'function') {
            console.log('condition需為一個函數');
            return false;
        }

        return true;
    }

    _createDropdown(binder, chatOption, isLeft = true) {
        let dropdown = document.createElement('div');
        dropdown.classList.add('chat-room-dropdown');
        dropdown.innerHTML = `<div class="chat-room-dropdown-options-icon">${chatOption().icon}</div> <div class="chat-room-dropdown-options-block"></div>`;
        let dropdownOptions = dropdown.querySelector('.chat-room-dropdown-options-block');
        let dropdownIcon = dropdown.querySelector('.chat-room-dropdown-options-icon');

        // 根據點擊的位置判斷如何處理關閉選單
        const closeDropdown = (ele) => {
            if (ele.parentElement) {
                ele.parentElement.classList.remove('dropdown-display-block');
            } else {
                dropdown.classList.remove('dropdown-display-block');
            }
            if (ele.nextElementSibling) {
                ele.nextElementSibling.classList.remove('dropdown-display-block');
            } else {
                dropdownOptions.classList.remove('dropdown-display-block');
            }
            this._dropdownVisible.isOpen = false;
        };

        // 開啟選單
        const openDropdown = e => {
            dropdown.classList.add('dropdown-display-block');
            dropdownOptions.classList.add('dropdown-display-block');


            // 判斷底下空間是否足夠
            const dropdownRect = dropdown.getBoundingClientRect();
            const optionsRect = dropdownOptions.getBoundingClientRect();
            const viewportHeight = window.innerHeight;

            if (dropdownRect.bottom + optionsRect.height > viewportHeight) {
                dropdownOptions.style.top = `calc(0% - ${optionsRect.height}px + 1em)`;
            } else {
                dropdownOptions.style.top = 'calc(100% + 5px)';
            }
            this._dropdownVisible.isOpen = true;
            this._dropdownVisible.target = e.currentTarget;
        }

        // 點擊開啟或是關閉下拉選單的事件
        dropdownIcon.addEventListener('click', e => {
            e.stopPropagation();
            if (this._dropdownVisible.isOpen && this._dropdownVisible.target === e.currentTarget) {
                closeDropdown(this._dropdownVisible.target);
            } else if (this._dropdownVisible.isOpen) {
                closeDropdown(this._dropdownVisible.target);
                openDropdown(e);
            } else {
                openDropdown(e);
            }
        });

        // 點其他位置都直接關閉
        document.addEventListener('click', closeDropdown);

        // 判斷是否有設定寬度參數
        let width = chatOption().width;
        if (width) {
            if (isLeft) {
                this.chatContainerElement.style.setProperty('--weien-chat-room-card-dropdown-left-option-size', width);
            } else {
                this.chatContainerElement.style.setProperty('--weien-chat-room-card-dropdown-right-option-size', width);
            }
        }
        // 判斷是否有設定邊距大小
        let padding = chatOption().padding;
        if (padding) {
            if (isLeft) {
                this.chatContainerElement.style.setProperty('--weien-chat-room-card-dropdown-left-option-padding', padding);
            } else {
                this.chatContainerElement.style.setProperty('--weien-chat-room-card-dropdown-right-option-padding', padding);
            }

        }

        chatOption().options.forEach(option => {
            let dropdownOption = document.createElement('div');
            dropdownOption.classList.add('chat-room-dropdown-option');
            dropdownOption.innerHTML = `
                <div class="chat-room-dropdown-option-text">
                    ${option.text}
                </div>
            `;
            if (option.bind && this._validateOptionsBind(option.bind, binder)) {
                let text = dropdownOption.querySelector('.chat-room-dropdown-option-text');
                binder.bindElement(
                    option.bind.property,
                    text,
                    (el) => {
                        if (option.bind.condition(binder)) {
                            el.innerHTML = `${option.bind.text[0]}`;
                        } else {
                            el.innerHTML = `${option.bind.text[1]}`;
                        }
                    }
                );
            }
            if (option.slider) {
                let slider = this._createSlider();
                binder.bindElement(
                    option.slider,
                    slider,
                    (el, value) => {
                        if (value) {
                            el.classList.add('chat-room-slider-on');
                        } else {
                            el.classList.remove('chat-room-slider-on');
                        }
                    }
                )
                dropdownOption.append(slider);
            }
            dropdownOption.addEventListener('click', e => {
                option.click(binder, e);
            });
            dropdownOptions.append(dropdownOption);
        })
        return dropdown;
    }

    _createSlider() {
        let slider = document.createElement('div');
        slider.classList.add('chat-room-slider');
        slider.innerHTML = '<div class="chat-room-slider-button"></div>';
        return slider;
    }
}

// 對html元素與變數進行響應式綁定
class Binder {
    constructor(value) {
        this.value = this._makeReactive(value);
    }

    _makeReactive(data) {
        const handler = {
            set: (target, property, value) => {
                target[property] = value;
                this._updateElements(property, value);
                return true;
            },
            get: (target, property) => {
                if (Array.isArray(target) && this._isArrayMethod(property)) {
                    return (...args) => {
                        const result = Array.prototype[property].apply(target, args);
                        this._updateElements(property, target);
                        return result;
                    };
                }
                return target[property];
            }
        };
        return new Proxy(data, handler);
    }

    _isArrayMethod(property) {
        return ['push', 'pop', 'shift', 'unshift', 'splice', 'sort', 'reverse'].includes(property);
    }

    bindElement(property, element, updateFunction = (el, value) => { el.textContent = value; }) {
        if (!this.elements) {
            this.elements = {};
        }
        if (!this.elements[property]) {
            this.elements[property] = [];
        }
        this.elements[property].push({ element, updateFunction });

        updateFunction(element, this.value[property]);

        if (element.tagName === "INPUT" || element.tagName === "TEXTAREA") {
            element.value = this.value[property];
            element.addEventListener("input", (event) => {
                this.value[property] = event.target.value;
            });
        }
    }

    _updateElements(property, value) {
        if (this.elements && this.elements[property]) {
            this.elements[property].forEach(({ element, updateFunction }) => {
                updateFunction(element, value);
            });
        }
    }
}