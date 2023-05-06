from enum import Enum

class AppType(Enum):
    APPLICATION = "APPLICATION"
    PREDICTOR = "PREDICTOR"


class Command(Enum):
    LOG = "LOG"
    DATA = "DATA"
    IMAGE = "IMAGE"
    COMMAND = "COMMAND"
    OTHER = "OTHER"
