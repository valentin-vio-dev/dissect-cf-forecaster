export const getElement = id => document.getElementById(id);

export const elements = {
    selectPredictors: getElement('select-predictors'),
    inputChunkSize: getElement('input-chunk-size'),
    inputTrainSize: getElement('input-train-size'),
    inputSmoothing: getElement('input-smoothing'),
    containerHyperParameters: getElement('container-hyper-parameters'),
    textaeraPredictor: getElement('textarea-predictor-terminal'),

    inputHost: getElement('input-socket-host'),
    inputPort: getElement('input-socket-port'),
    inputOutputLocation: getElement('input-output-location'),
    checkboxSaveDataset: getElement('checkbox-save-dataset'),
    checkboxSaveStandalone: getElement('checkbox-save-standalone'),
    checkboxSaveDatasetImage: getElement('checkbox-save-dataset-image'),
    textaeraConfig: getElement('textarea-config'),
    checkboxSaveConfig: getElement('checkbox-save-config'),
    textaeraApplication: getElement('textarea-application-terminal'),

    labelDuration: getElement('label-duration'),

    buttonRun: getElement('button-run'),

    predictionImage: getElement('prediction-image'),


    inputTrainingDatasetLoc: getElement('input-training-dataset-location'),
    inputModelOutLoc: getElement('input-model-output-location'),
    inputWindowSize: getElement('input-window-size'),
    inputSmoothingModel: getElement('input-smoothing-model'),
    inputEpochs: getElement('input-epochs'),
    inputOptimizer: getElement('input-optimizer'),
    inputLossFunction: getElement('input-loss-function'),
    inputFeature: getElement('input-feature'),

    buttonTrainModel: getElement('button-train-model'),

    modal: getElement('modal'),
    modalWrapper: getElement('modal-wrapper')
};