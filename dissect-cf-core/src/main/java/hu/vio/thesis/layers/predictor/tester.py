def func(args):
    args_array = []
    for key in args:
        args_array.append(f"{key}={args[key]}")
        print(key, "-", args[key])
    print(" ".join(args_array))


func({
    "host": "127.0.0.1",
    "port": "76535"
})