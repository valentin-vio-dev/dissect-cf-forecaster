export const callApi = (key) => {
    if (!window?.pywebview?.api) {
        return new Function();
    }
    return window.pywebview.api[key];
};



