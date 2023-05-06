export const createTimer = (tickCallback) => {
    let time = 1;
    let interval = null;
    return {
        start: function() {
            interval = setInterval(() => {
                if (tickCallback) tickCallback(time);
                time++;
            }, 1000);
            return this;
        },
        pause: function() {
            clearInterval(interval);
            return this;
        },
        reset: function() {
            clearInterval(interval);
            time = 1;
            interval = null;
            return this;
        }
    };
};

export const getFormatedTime = (time) => {
    const seconds = time % 60 < 10 ? `0${time % 60}` : time % 60;
    const mintues = parseInt(time / 60) % 60 < 10 ? `0${parseInt(time / 60) % 60}` : parseInt(time / 60) % 60;
    const hours = parseInt(parseInt(time / 60) / 60) % 60 < 10 ? `0${parseInt(parseInt(time / 60) / 60) % 60}` : parseInt(parseInt(time / 60) / 60) % 60;
    return `${hours}:${mintues}:${seconds}`;
};