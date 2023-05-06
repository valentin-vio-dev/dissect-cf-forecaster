export const getAppConfiguration = () => {
    return {
        predictor: null,
        chunkSize: null,
        trainSize: null,
        smoothing: null,
        hyperParameters: null,
        host: null,
        port: null,
        outputLocation: null,
        saveDataset: null,
        saveStandalone: null,
        saveDatasetImage: null,
        variableConfig: null,
        saveConfig: null
    };
};

export const getTrainConfiguration = () => {
    return {
        datasetLocation: null,
        modelOutput: null,
        windowSize: null,
        smoothing: null,
        epochs: null,
        optimizer: null,
        lossFunction: null,
        feature: null
    }
}