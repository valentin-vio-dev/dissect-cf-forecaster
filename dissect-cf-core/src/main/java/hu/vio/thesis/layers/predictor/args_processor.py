def process_args(args):
    processed = dict()

    for arg in args:
        if "=" in arg:
            pieces = arg.split("=")
            key, value = pieces[0].replace("--", ""), pieces[1]
            processed[key] = value
        else:
            key = arg.replace("--", "")
            processed[key] = True

    return processed
