from datetime import datetime


class Logger:

    @staticmethod
    def log(data):
        date = datetime.today().strftime('%Y/%m/%d %H:%M:%S')
        print(f"[{date}]\t{data}")
