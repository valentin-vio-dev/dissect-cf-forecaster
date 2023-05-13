import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

from thesis.layers.predictor.utils import Utils

df = pd.read_csv("D:\DissectCF_Repository\Új mappa\lstm\error_metrics.csv", sep=";", decimal=",")
df.drop(columns=df.columns[0], axis=1, inplace=True)
feature_dfs = []
ca_dfs = []

for i in range(0, len(df.columns), 3):
    feature_dfs.append(df.iloc[:, i:i + 3])

for i in range(0, len(feature_dfs), 3):
    ca = [feature_dfs[i], feature_dfs[i + 1], feature_dfs[i + 2]]
    ca_dfs.append(ca)


fig, axs = plt.subplots(int(len(df.columns) / 3 / 3), 3)
fig.set_size_inches(16, 19)

def splitSerToArr(ser):
    return [ser.index, ser.as_matrix()]


x = [i for i in range(0, 20)]
for i in range(0, len(ca_dfs)):
    columns = ca_dfs[i][0].columns.to_list()
    name = Utils.replace_all(columns[0].split("__")[0], "_", " ").capitalize()
    print(name)
    for j in range(0, len(ca_dfs[0])):

        #print(ca_dfs[i][j].iloc[:, 0].values)
        axs[i][j].plot(
            x,
            ca_dfs[i][j].iloc[:, 0].values,
            color="r",
            label="RMSE",
        )
        axs[i][j].plot(
            x,
            ca_dfs[i][j].iloc[:, 1].values,
            color="g",
            label="MAE",
        )

        axs[i][j].plot(
            x,
            ca_dfs[i][j].iloc[:, 2].values,
            color="b",
            label="MSE",
        )
        axs[i][j].xaxis.set_label_position("top")
        if i == 0:
            if j == 0:
                axs[i][j].set_xlabel("Memory")
            elif j == 1:
                axs[i][j].set_xlabel("Load of resource")
            elif j == 2:
                axs[i][j].set_xlabel("Total processing power")

        if j == 0:
            axs[i][j].set_ylabel(name)
        axs[i][j].grid()
        axs[i][j].legend(loc="lower right")
        #axs[i][j].set_yscale("log")




plt.savefig("wrhwrhrwrh")
fig.show()


#plt.plot(df[df.columns[0]], df[df.columns[2]])
