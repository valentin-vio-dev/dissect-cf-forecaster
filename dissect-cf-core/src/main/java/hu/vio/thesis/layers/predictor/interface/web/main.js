import { createTimer, getFormatedTime } from './js/timer.js';
import { getAppConfiguration, getTrainConfiguration } from './js/app_configuration.js';
import { elements, getElement } from './js/elements.js';
import { callApi } from './js/api.js';
import { getDefaultConfig, getVariableConfig } from './js/configuration.js';
import { addHyperParameterFields, getHyperParameterDictionary, getHyperParameterElements } from './js/hyper_parameter_utils.js';
import { convertValue, getCurrentDateString, replaceAll } from './js/utils.js';


window.modalOpen = false;
window.trainingModel = false;

const addTerminalMessage = (terminalMessages, message) => {
    if (!running) {
        return;
    }

    if (terminalMessages.length > 50) {
        terminalMessages.shift();
    }

    terminalMessages.push({ time: Date.now(), message });
};


let appConfiguration = getAppConfiguration();
let trainConfiguration = getTrainConfiguration();
let terminalApplicationMessages = [];
let terminalPredictorMessages = [];
let running = false;
let timer = createTimer((time) => {
    elements.labelDuration.innerHTML = getFormatedTime(time);
});


elements.selectPredictors.addEventListener('change', e => {
    addHyperParameterFields(e.target.value);
    appConfiguration.predictor = e.target.value;
});

const setRepositoryText = () => {
    elements.inputOutputLocation.value = `D:/DissectCF_Repository/${getCurrentDateString()}`
};

setRepositoryText()

elements.buttonRun.onclick = async () => {
    running = !running;

    if (running) {
        timer.start();

        appConfiguration.chunkSize = convertValue(elements.inputChunkSize.value);
        appConfiguration.trainSize = convertValue(elements.inputTrainSize.value);
        appConfiguration.smoothing = convertValue(elements.inputSmoothing.value);
        appConfiguration.hyperParameters = getHyperParameterDictionary(appConfiguration);
        appConfiguration.host = elements.inputHost.value
        appConfiguration.port = elements.inputPort.value
        appConfiguration.outputLocation = elements.inputOutputLocation.value;
        appConfiguration.saveDataset = convertValue(elements.checkboxSaveDataset.checked);
        appConfiguration.saveStandalone = convertValue(elements.checkboxSaveStandalone.checked);
        appConfiguration.saveDatasetImage = convertValue(elements.checkboxSaveDatasetImage.checked);
        appConfiguration.variableConfig = getVariableConfig(elements.textaeraConfig.value);
        appConfiguration.saveConfig = convertValue(elements.checkboxSaveConfig.checked);

        elements.buttonRun.innerText = 'Stop';
        elements.buttonRun.classList.add('active');
        elements.buttonRun.classList.remove('inactive');
        elements.selectPredictors.disabled = true;
        elements.inputChunkSize.disabled = true;
        elements.inputTrainSize.disabled = true;
        elements.inputSmoothing.disabled = true;
        elements.inputHost.disabled = true;
        elements.inputPort.disabled = true;
        elements.inputOutputLocation.disabled = true;
        elements.checkboxSaveDataset.disabled = true;
        elements.checkboxSaveStandalone.disabled = true;
        elements.checkboxSaveDatasetImage.disabled = true;
        elements.textaeraConfig.disabled = true;
        elements.checkboxSaveConfig.disabled = true;
        elements.textaeraApplication.innerHTML = '';

        getHyperParameterElements(appConfiguration).forEach(element => element.disabled = true);
        console.log(appConfiguration)
        await callApi('set_config')(appConfiguration);
    } else {
        await callApi('close_application_layer')();

        timer.reset();
        terminalApplicationMessages = [];
        terminalPredictorMessages = [];
        setRepositoryText();

        elements.labelDuration.innerHTML = '00:00:00';
        elements.buttonRun.innerText = 'Run';
        elements.buttonRun.classList.add('inactive');
        elements.buttonRun.classList.remove('active');
        elements.selectPredictors.disabled = false;
        elements.inputChunkSize.disabled = false;
        elements.inputTrainSize.disabled = false;
        elements.inputSmoothing.disabled = false;
        elements.inputHost.disabled = false;
        elements.inputPort.disabled = false;
        elements.inputOutputLocation.disabled = false;
        elements.checkboxSaveDataset.disabled = false;
        elements.checkboxSaveStandalone.disabled = false;
        elements.checkboxSaveDatasetImage.disabled = false;
        elements.textaeraConfig.disabled = false;
        elements.checkboxSaveConfig.disabled = false;
        elements.predictionImage.src = '';

        getHyperParameterElements(appConfiguration).forEach(element => element.disabled = false);
    }
};

elements.textaeraConfig.innerHTML = getDefaultConfig();
elements.selectPredictors.dispatchEvent(new Event('change'));

const handlePredictorData = (incoming) => {
    if (incoming['command'] === 'LOG') {
        addTerminalMessage(terminalPredictorMessages, incoming['message']);
    } else if (incoming['command'] === 'DATA') {
        if (incoming['event'] === 'prediction-avg-rmse') {
            elements.textRMSE.innerText = incoming['message'];
        } else if (incoming['event'] === 'prediction-avg-mae') {
            elements.textMAE.innerText = incoming['message'];
        } else if (incoming['event'] === 'prediction-avg-mse') {
            elements.textMSE.innerText = incoming['message'] == null ? "-" : incoming['message'];
        }
    } else if (incoming['command'] === 'IMAGE') {
        elements.predictionImage.src = incoming['message'];
    } else if (incoming['command'] === 'COMMAND') {
        if (incoming['event'] === 'model-training-status' && incoming['message'] === 'completed') {
            window.modalOpen = false;
            elements.modalWrapper.classList.remove('open');
            elements.modalWrapper.classList.add('hide');

            window.trainingModel = false;
            elements.inputTrainingDatasetLoc.disabled = false;
            elements.inputModelOutLoc.disabled = false;
            elements.inputWindowSize.disabled = false;
            elements.inputSmoothingModel.disabled = false;
            elements.inputEpochs.disabled = false;
            elements.inputOptimizer.disabled = false;
            elements.inputLossFunction.disabled = false;
            elements.inputFeature.disabled = false;

            elements.buttonTrainModel.disabled = false;
            window.trainingModel = false;

            getElement('ltsm-model_location').value = elements.inputModelOutLoc.value;
        }
    } else if (incoming['command'] === 'OTHER') {

    }
};

const handleApplicationData = (incoming) => {
    if (incoming['command'] === 'LOG') {
        addTerminalMessage(terminalApplicationMessages, incoming['message']);
    } else if (incoming['command'] === 'DATA') {

    } else if (incoming['command'] === 'IMAGE') {

    } else if (incoming['command'] === 'COMMAND') {

    } else if (incoming['command'] === 'OTHER') {

    }
};

elements.modalWrapper.onclick = (event) => {
    if (event.target === elements.modalWrapper && !window.trainingModel) {
        window.modalOpen = false;
        elements.modalWrapper.classList.remove('open');
        elements.modalWrapper.classList.add('hide');
    }
};

elements.modal.addEventListener("click", (event) => {
    event.stopPropagation();
});


elements.buttonTrainModel.onclick = async () => {
    elements.inputTrainingDatasetLoc.disabled = true;
    elements.inputModelOutLoc.disabled = true;
    elements.inputWindowSize.disabled = true;
    elements.inputSmoothingModel.disabled = true;
    elements.inputEpochs.disabled = true;
    elements.inputOptimizer.disabled = true;
    elements.inputLossFunction.disabled = true;
    elements.inputFeature.disabled = true;

    elements.buttonTrainModel.disabled = true;
    window.trainingModel = true;

    trainConfiguration.datasetLocation = convertValue(elements.inputTrainingDatasetLoc.value);
    trainConfiguration.modelOutput = convertValue(elements.inputModelOutLoc.value);
    trainConfiguration.windowSize = convertValue(elements.inputWindowSize.value);
    trainConfiguration.smoothing = convertValue(elements.inputSmoothingModel.value);
    trainConfiguration.epochs = convertValue(elements.inputEpochs.value);
    trainConfiguration.optimizer = convertValue(elements.inputOptimizer.value);
    trainConfiguration.lossFunction = convertValue(elements.inputLossFunction.value);
    trainConfiguration.feature = convertValue(elements.inputFeature.value);

    console.log(trainConfiguration);

    await callApi('set_training_model_config')(trainConfiguration);
}





window.onload = () => {
    setInterval(async () => {
        const data = await callApi('get_message')();
        if (data['layer'] === 'APPLICATION') {
            handleApplicationData(data);
        } else if (data['layer'] === 'PREDICTOR') {
            handlePredictorData(data);
        }
    }, 10);

    setInterval(async () => {
        const existsDataset = await callApi('check_model')(getElement('ltsm-model_location').value);
        if (existsDataset) {
            getElement('ltsm-model_location_hint').style.display = 'none';
            getElement('ltsm-model_row').style.display = 'none';
        } else {
            getElement('ltsm-model_location_hint').style.display = 'flex';
            getElement('ltsm-model_row').style.display = 'flex';
        }
    }, 10);

    // checker
    setInterval(() => {
        for (let i = 0; i < terminalApplicationMessages.length; i++) {
            if (Date.now() - terminalApplicationMessages[i].time > 5 * 1000) {
                terminalApplicationMessages.splice(i, 1);
            }
        }

        elements.textaeraApplication.innerHTML = '';
        terminalApplicationMessages.forEach(message => {
            elements.textaeraApplication.innerHTML += `> [T-${message.time}] ${message.message}\n`;
        });
        elements.textaeraApplication.scrollTop = elements.textaeraApplication.scrollHeight

        /////

        for (let i = 0; i < terminalPredictorMessages.length; i++) {
            if (Date.now() - terminalPredictorMessages[i].time > 5 * 1000) {
                terminalPredictorMessages.splice(i, 1);
            }
        }

        elements.textaeraPredictor.innerHTML = '';
        terminalPredictorMessages.forEach(message => {
            elements.textaeraPredictor.innerHTML += `> [T-${message.time}] ${message.message}\n`;
        });
        elements.textaeraPredictor.scrollTop = elements.textaeraPredictor.scrollHeight
    }, 10);
};



if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
    window.document.body.setAttribute('data-theme', 'dark');
}

window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', event => {
    const newColorScheme = event.matches ? "dark" : "light";
    if (newColorScheme === 'dark') {
        window.document.body.setAttribute('data-theme', 'dark');
    } else {
        window.document.body.setAttribute('data-theme', 'light');
    }
});

