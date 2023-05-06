export const replaceAll = (str, find, replace) => {
    return str.replace(new RegExp(find, 'g'), replace);
};

export const convertValue = value => {
    if (value === '') return '';
    if (value === 'true' || value === true) return true;
    if (value === 'false' || value === false) return false;

    if (!isNaN(Number(value))) {
        if (value.includes('.')) {
            return parseFloat(value);
        }
        return parseInt(value);
    } 

    return value;
};

export const getCurrentDateString = () => {
    const date = new Date().toLocaleString('en-En', {
      hour12: false,
    });

    return replaceAll(replaceAll(replaceAll(date, '/', '_'), ', ', '_'), ':', '_');
}