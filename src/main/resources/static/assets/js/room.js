let messageContainer = document.getElementById('messages')

let APP_ID = '9026d01943724f1c8178e41aa938e367'
let token = null


let currentUser = document.getElementById('current-user').innerText.trim()
let uid

if (currentUser === "Account") {
	uid = 'GUEST' + String(Math.floor(Math.random() * 10000))
}else {
	uid = currentUser
}

let goBackToLobbyBtn = document.getElementById('create__room__btn')
let host = false
let hostId
let rtmClient
let hostID = sessionStorage.getItem("room_id")
let roomName = sessionStorage.getItem("room_id")
let displayName = sessionStorage.getItem("display_name")

let urlParams = window.location.pathname
let room = urlParams.substring(6, urlParams.length)

let avatar = sessionStorage.getItem('avatar')

let initiate = async () => {
    AgoraRTC.disableLogUpload()
    AgoraRTC.setLogLevel(4)

    AgoraRTM.LOG_FILTER_OFF
    
    rtmClient = await AgoraRTM.createInstance(APP_ID)
	await rtmClient.login({ uid, token })

    try {
        let attributes = await rtmClient.getChannelAttributesByKeys(room, ['room_name', 'host_id'])
        roomName = attributes.room_name.value
        hostId = attributes.host_id.value
        if (uid === hostId) {
            host = true
            document.getElementById('stream__controls').style.display = 'flex'
        }
    } catch (error) {
        await rtmClient.setChannelAttributes(room, { 'room_name': roomName, 'host': uid, 'host_image': avatar, 'host_id': hostID })
        host = true
        document.getElementById('stream__controls').style.display = 'flex'
    }

    const channel = await rtmClient.createChannel(room)
    await channel.join()

    await rtmClient.addOrUpdateLocalUserAttributes({ 'name': uid })

    const lobbyChannel = await rtmClient.createChannel('lobby')
    await lobbyChannel.join()

    lobbyChannel.on('MemberJoined', async (memberId) => {
        let participants = await channel.getMembers()

        if (participants[0] === uid) {
            let lobbyMembers = await lobbyChannel.getMembers()
            for (let i = 0; i < lobbyMembers.length; i++) {
                rtmClient.sendMessageToPeer({ text: JSON.stringify({ 'room': room, 'type': 'room_added' }) }, lobbyMembers[i])
            }
        }
    })

    channel.on('ChannelMessage', async (messageData, memberId) => {
        let data = JSON.parse(messageData.text)
        let name = data.displayName
        let myAvatar = data.avatar
        addMessageToDom(data.message, memberId, name, myAvatar)

    })

    let getParticipants = async () => {
        let participants = await channel.getMembers()

        if (participants.length <= 1) {
            let lobbyMembers = await lobbyChannel.getMembers()
            for (let i = 0; i < lobbyMembers.length; i++) {
                rtmClient.sendMessageToPeer({ text: JSON.stringify({ 'room': room, 'type': 'room_added' }) }, lobbyMembers[i])
            }
        }
    }

    let goBackToLobby = () => {
        window.location = `join.html`
    }

    getParticipants()


    let leaveChannel = async () => {
        await channel.leave()
        await rtmClient.logout()
    }

    window.addEventListener('beforeunload', leaveChannel)

    let addMessageToDom = async (messageData, memberId, displayName, avatar) => {
        let created = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
        if (created.startsWith("0")) {
            created.substring(1)
        }
        let messagesWrapper = document.getElementById('messages')
        let messageItem = `
                        <div class="message__wrapper">
                        <img class="avatar__md" src="/assets/images/default.PNG">
                        <div class="message__body">
                            <strong class="message__author">${displayName}</strong>
                            <small class="message__timestamp">${created}</small>
                            <p class="message__text">${messageData}</p>
                        </div>
                    </div>`
        messagesWrapper.insertAdjacentHTML('beforeend', messageItem)
        messageContainer.scrollTop = messageContainer.scrollHeight
    }

    let sendMessage = async (e) => {
        e.preventDefault()
        let message = e.target.message.value
        channel.sendMessage({ text: JSON.stringify({ 'message': message, 'displayName': uid, 'avatar': avatar }) })
        addMessageToDom(message, uid, uid, avatar)
        e.target.reset()
    }

    let messageForm = document.getElementById('message__form')
    messageForm.addEventListener('submit', sendMessage)

    /*goBackToLobbyBtn.addEventListener('click', () => {
        leaveChannel()
        sessionStorage.clear()
        window.location = `lobby.html`
    })
    */
}

let rtcUid = 'HOST' + Math.floor(Math.random() * 10000)
let config = {
	appID: APP_ID,
	token: null,
	uid: rtcUid,
	channel: room
}


let localTracks = []
let localScreenTracks

let streaming = false
let sharingScreen = false

let rtcClient = AgoraRTC.createClient({ mode: 'live', codec: 'vp8' })
let initiateRtc = async () => {
	console.log(config.channel)
    await rtcClient.join(config.appID, config.channel, config.token, config.uid)

    rtcClient.on('user-published', handleUserPublished)
    rtcClient.on('user-unpublished', handleUserLeft)
}

let toggleStream = async () => {
    if (!streaming) {
        startStream()
    } else {
        endStream()
    }
}

let startStream = async () => {
    streaming = true
    document.getElementById('stream-btn').innerHTML = 'Stop Stream'
    document.getElementById('stream-btn').style.color = 'red'

    rtcClient.setClientRole('host')

    localTracks = await AgoraRTC.createMicrophoneAndCameraTracks({}, {
        encoderConfig: {
            width: { min: 640, ideal: 1920, max: 1920 },
            height: { min: 480, ideal: 1080, max: 1080 }
        }
    })

    document.getElementById('video__stream').innerHTML = ''

    let player = `
                <div class="video-container" style="position:absolute; "id="user-container-${rtcUid}">
                    <div class="video-player" id="user-${rtcUid}">
                    </div>
                </div>
                `
    document.getElementById('video__stream').insertAdjacentHTML('beforeend', player)
    localTracks[1].play(`user-${rtcUid}`)

    await rtcClient.publish([localTracks[0], localTracks[1]])
}

let endStream = async () => {
    streaming = false

    document.getElementById('stream-btn').innerHTML = 'Start Stream'
    document.getElementById('stream-btn').style.color = 'blue'

    for (let i = 0; i < localTracks.length; i++) {
        localTracks[i].stop()
        localTracks[i].close()
    }

    if (sharingScreen) {
        localScreenTracks.close();
        await localScreenTracks.leave();
        await rtcClient.unpublish([localScreenTracks])
    }

    await rtcClient.unpublish([localTracks[0], localTracks[1]])

    let player = `
                <img src="/assets/images/stream-thumbnail.png" style="width:1090px; height: 600px;" alt="">
                `
    document.getElementById('video__stream').insertAdjacentHTML('beforeend', player)
}

let handleUserPublished = async (user, mediaType) => {
    await rtcClient.subscribe(user, mediaType)

    let player = document.getElementById('video__stream')
    if (player != null) {
        player.innerHTML = ''
    }
    if (mediaType === 'video') {
        player = `
                <div class="video-container" style="position:absolute; "id="user-container-${user.uid}">
                        <div class="video-player" id="user-${user.uid}">
                        </div>
                </div>
                    `
        document.getElementById('video__stream').insertAdjacentHTML('beforeend', player)
        user.videoTrack.play(`user-${user.uid}`)
    }

    if (mediaType === 'audio') {
        user.audioTrack.play()
    }

}

let handleUserLeft = async (user) => {
    document.getElementById(`video__stream`).innerHTML = ` <img src="/assets/images/stream-thumbnail.png" style="width:1080px; height: 600px;" alt="">`
}

let mute = false
let toggleMic = async (e) => {
    let button = e.currentTarget
    
    if (mute) {
        localTracks[0] = await AgoraRTC.createMicrophoneAudioTrack()
        await rtcClient.publish([localTracks[0]])
        button.classList.add('active')
        mute = false
    } else {
        localTracks[0].stop()
        localTracks[0].close()
        button.classList.remove('active')
        mute = true
    }

}

let toggleCamera = async (e) => {
    let button = e.currentTarget

    if (localTracks[1].muted) {
        await localTracks[1].setMuted(false)
        button.classList.add('active')
    } else {
        await localTracks[1].setMuted(true)
        button.classList.remove('active')

    }
}

let toggleScreen = async (e) => {
    if (sharingScreen) {
        sharingScreen = false
        await rtcClient.unpublish([localScreenTracks])

        document.getElementById('video__stream').innerHTML = ''

        let player = `
                    <div class="video-container" style="position:absolute; "id="user-container-${rtcUid}">
                        <div class="video-player" id="user-${rtcUid}">
                        </div>
                    </div>
                    `
        document.getElementById('video__stream').insertAdjacentHTML('beforeend', player)

        localTracks[1].play(`user-${rtcUid}`)
        await rtcClient.publish([localTracks[1]])
        document.getElementById('screen-btn').style.backgroundColor = 'black'
    } else {
        sharingScreen = true
        document.getElementById('screen-btn').style.backgroundColor = 'orangered'
        localScreenTracks = await AgoraRTC.createScreenVideoTrack()
        document.getElementById('video__stream').innerHTML = ''

        let player = document.getElementById(`user-container-${rtcUid}`)
        if (player != null) {
            player.innerHTML = ''
        }
        player = `
                <div class="video-container" style="position:absolute; "id="user-container-${rtcUid}">
                    <div class="video-player" id="user-${rtcUid}">
                    </div>
                </div>
                `
        document.getElementById('video__stream').insertAdjacentHTML('beforeend', player)
        localScreenTracks.play(`user-${rtcUid}`)
        await rtcClient.unpublish([localTracks[1]])
        await rtcClient.publish([localScreenTracks])
        
    }
}



document.getElementById('camera-btn').addEventListener('click', toggleCamera)
document.getElementById('mic-btn').addEventListener('click', toggleMic)
document.getElementById('screen-btn').addEventListener('click', toggleScreen)
document.getElementById('stream-btn').addEventListener('click', toggleStream)

initiate()
initiateRtc()