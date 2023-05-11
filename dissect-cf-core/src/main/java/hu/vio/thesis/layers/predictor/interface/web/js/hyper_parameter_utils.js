import { elements } from './elements.js';
import { replaceAll, convertValue } from './utils.js';


const toggleModal = () => {
    window.modalOpen = !window.modalOpen;
    if (window.modalOpen) {
        elements.modalWrapper.classList.remove('hide');
        elements.modalWrapper.classList.add('open');
    } else {
        elements.modalWrapper.classList.remove('open');
        elements.modalWrapper.classList.add('hide');
    }
}

const HYPER_PARAMETERS = {
    arima: [
        { label: 'P value', value: '2', type: 'number', min: '0' },
        { label: 'D value', value: '0', type: 'number', min: '0' },
        { label: 'Q value', value: '0', type: 'number', min: '0' },
        { label: 'Alpha', value: '0.05', type: 'number', step: '.01' }
    ],
    forecaster_autoreg: [
        { label: 'Random state', value: '123', type: 'number', min: '0' },
        { label: 'Lags', value: '128', type: 'number', min: '0' }
    ],
    holt_winters: [
        { label: 'Trend', value: 'add', type: 'text', hint: 'Values: add, mul' },
        /*{ label: 'Seasonal', value: 'add', type: 'text', hint: 'Values: add, mul' },
        { label: 'Seasonal periods', value: '60', type: 'number', min: '0' }*/
    ],
    ltsm: [
        { label: 'Model location', value: 'D:/dev/dataset', type: 'text', hint: 'Configuration file not found. Train a model in order to predict data!'},
        { label: 'Model', value: 'Model settings', type: 'button', callback: () => {
            toggleModal();
        } },
    ],
    neural_prophet: [],
    fb_prophet: [],
};

const getHyperParameterId = (predictor, label) => predictor.toLowerCase() + '-' + replaceAll(label, ' ', '_').toLowerCase();

export const addHyperParameterFields = (predictor) => {
    while (elements.containerHyperParameters.firstChild) {
        elements.containerHyperParameters.removeChild(elements.containerHyperParameters.lastChild);
    }

    const fields = HYPER_PARAMETERS[predictor];

    if (!fields) {
        return;
    }

    fields.forEach(field => {
        const id = getHyperParameterId(predictor, field.label);
        
        const row = document.createElement('div');
        row.classList.add('row')
        row.id = id + '_row';

        const label = document.createElement('label');
        label.innerText = field.label;

        const input = document.createElement('input');
        input.type = field.type;
        input.value = convertValue(field.value);
        input.id = id;

        if (field.callback) {
            input.onclick = field.callback;
        }

        if (field.step) {
            input.setAttribute('step', field.step);
        }

        if (field.min) {
            input.setAttribute('min', field.min);
        }

        if (field.max) {
            input.setAttribute('max', field.max);
        }

        const inputContainer = document.createElement('div');

        inputContainer.appendChild(input);
        inputContainer.classList.add('input-container');

        if (field.hint) {
            const hint = document.createElement('small');
            hint.id = id + '_hint'
            hint.classList.add('input-hint');
            hint.innerText = field.hint;
            inputContainer.appendChild(hint);
        }

        row.appendChild(label);
        row.appendChild(inputContainer);

        elements.containerHyperParameters.appendChild(row);
    });
};

export const getHyperParameterElements = (appConfiguration) => {
    const fields = HYPER_PARAMETERS[appConfiguration.predictor];
   
    if (!fields) {
        return;
    }

    const elements = [];
    fields.forEach(field => {
        const id = getHyperParameterId(appConfiguration.predictor, field.label);
        elements.push(document.getElementById(id));
    });

    return elements;
}

export const getHyperParameterDictionary = (appConfiguration) => {
    const result = {};

    getHyperParameterElements(appConfiguration).forEach(element => {
        result[element.id] = convertValue(element.value);
    });

    return Object.keys(result).length > 0 ? result : null;
}