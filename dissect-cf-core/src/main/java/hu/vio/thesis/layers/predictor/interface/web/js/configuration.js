import { replaceAll, convertValue } from "./utils.js";

export const getVariableConfig = value => {
    const elements = replaceAll(value, ' ', '').split('\n');

    const result = {};

    elements.forEach(el => {
        if (el.includes('=')) {
            const splitted = el.split('=');
            if (splitted[0] && splitted[1]) {
                result[splitted[0]] = convertValue(splitted[1]);
            }
        }
    });

    return Object.keys(result).length > 0 ? result : null;
};

export const getDefaultConfig = () => {
    return `node_range_cloud=${500 * 1000}
node_range_fog=${100 * 100}
app_freq=${60 * 1000 * 5}
task_size=${262144 * 100}
count_of_inst=${12000}
threshold=${1}
va_startup_process=${100}
va_req_disk=${1073741824}
fog_strategy_1=${'load'}
fog_strategy_2=${'push'}
arc1_cpu=${8}
arc2_cpu=${4}
arc1_processing=${0.001 / 100}
arc2_processing=${0.001 / 100}
arc1_memory=${4294967296}
arc2_memory=${4294967296}
disk_size=${1073741824}`
};
