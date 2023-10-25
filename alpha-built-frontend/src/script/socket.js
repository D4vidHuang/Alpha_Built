function constructSocket(url, userId, projectId) {
    const socket = new WebSocket(url)

    socket.addEventListener('error', (error) => {
        console.log('WebSocket error:', error);
    })

    return socket
}


function socketMessageHandler(scene, message) {
    switch (message.action) {
    }
}

export {constructSocket, socketMessageHandler}


