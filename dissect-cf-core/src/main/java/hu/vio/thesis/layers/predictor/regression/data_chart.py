import pandas as pd
import matplotlib.pyplot as plt
from os import listdir
from os.path import isfile, join
from datetime import datetime
import os


NODES_DIRECTORY = "D:\dev\dissect-cf\dissect-cf-core\src\main\java\hu/vio\simulator\predictor/tmp"
OUTPUT_IMAGE_DIRECTORY = NODES_DIRECTORY
FEATURES = []
DATE = datetime.now().strftime("%Y_%m_%d_%H_%M_%S")


def replace_commas(cell):
    return str(cell).replace(",", ".")


def get_all_node(path):
    files = [f for f in listdir(path) if isfile(join(path, f)) and f.split(".")[-1] == "csv"]
    result = []

    for file in files:
        file_name = file.split(".")[0]
        dataframe = pd.read_csv("{}\\{}".format(path, file), sep=";")

        global FEATURES
        if len(FEATURES) == 0:
            FEATURES = dataframe.columns.values[:-1]

        result.append({
            "name": file_name,
            "dataframe": dataframe
        })
    return result


def separate_features(nodes):
    for node in nodes:
        for feature_name in FEATURES:
            data = node["dataframe"][feature_name].apply(replace_commas).astype(float).tolist()
            node[feature_name] = data


def smooth(data, iter):
    for n in range(0, iter):
        for i in range(1, len(data)-1):
            data[i] = (data[i-1] + data[i+1]) / 2


def plot_nodes(nodes):
    global FEATURES, OUTPUT_IMAGE_DIRECTORY
    fig, axs = plt.subplots(len(FEATURES), len(nodes))

    for i, feature_name in enumerate(FEATURES):
        for j, node in enumerate(nodes):
            y = node[feature_name]
            x = [x for x in range(0, len(y))]

            axs[i, j].plot(x, y, color="red")

            if i == 0:
                axs[i, j].xaxis.set_label_position("top")
                axs[i, j].set_xlabel(node["name"].upper(), fontsize=20)

    fig.set_size_inches(8 * 18.5, 2 * 18.5)
    plt.savefig("{}\\{}".format(OUTPUT_IMAGE_DIRECTORY, "output.png"), dpi=512/8)


def move_all():
    new_dir = NODES_DIRECTORY.split("\\")
    new_dir.insert(len(new_dir) - 1, DATE)
    new_dir.remove("tmp")
    new_dir = "\\".join(new_dir)
    os.mkdir(new_dir)

    files = [f for f in listdir(NODES_DIRECTORY) if isfile(join(NODES_DIRECTORY, f))]
    for file in files:
        source = (NODES_DIRECTORY + "\\" + file)
        destination = (NODES_DIRECTORY + "\\" + file).split("\\")
        destination.insert(len(destination) - 1, DATE)
        destination.remove("tmp")
        destination = "\\".join(destination)
        print(destination)
        os.rename(source, destination)


if __name__ == "__main__":
    nodes = get_all_node(NODES_DIRECTORY)
    if len(nodes) > 0:
        separate_features(nodes)
        plot_nodes(nodes)
        #move_all()
    else:
        print("No data available in directory " + NODES_DIRECTORY + "!")
