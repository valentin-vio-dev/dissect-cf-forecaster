def create_message(command, event, data):
    message =  str({
        "layer": "PREDICTOR",
        "command": command,
        "event": event,
        "message": data
    }) + "\r\n"

    return bytes(message, 'utf-8')