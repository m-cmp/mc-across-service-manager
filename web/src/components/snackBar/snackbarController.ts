// snackbarController.ts
import { CustomError } from '@axios/axios';
import { OptionsObject, SnackbarKey, SnackbarMessage } from 'notistack';

let enqueueSnackbar: ((message: SnackbarMessage, options?: OptionsObject) => SnackbarKey) | null = null;

export const setEnqueueSnackbar = (
  enqueueSnackbarFunc: (message: SnackbarMessage, options?: OptionsObject) => SnackbarKey
) => {
  enqueueSnackbar = enqueueSnackbarFunc;
};

const messageCount: { [key: string]: number } = {};
const additionalMessages: { [key: string]: string[] } = {};
const timeoutId: { [key: string]: ReturnType<typeof setTimeout> | null } = {};
const messageLog: { [key: string]: string } = {};

export const displaySnackbar = (
  message: string,
  variant: OptionsObject['variant'] = 'success',
  response: CustomError | null = null,
  autoHideDuration: number | null = 5000
): SnackbarKey | null => {
  let snackbarKey: SnackbarKey | null = null;
  const url = response?.config.url;
  if (enqueueSnackbar) {
    if (url && variant === 'error') {
      if (messageLog[url] === message) {
        return null;
      }
      messageLog[url] = message;
      setTimeout(() => {
        delete messageLog[url];
      }, 300000);
      additionalMessages[message] = additionalMessages[message] || [];
      additionalMessages[message].push(url);
    }
    messageCount[message] = (messageCount[message] || 0) + 1;

    if (autoHideDuration === null || variant !== 'error') {
      snackbarKey = enqueueSnackbar(message, {
        variant,
        anchorOrigin: {
          vertical: 'bottom',
          horizontal: 'right',
        },
        autoHideDuration,
      });
    } else {
      if (timeoutId[message] && typeof timeoutId[message] === 'number') {
        clearTimeout(timeoutId[message] as ReturnType<typeof setTimeout>);
        timeoutId[message] = null;
      }

      timeoutId[message] = setTimeout(() => {
        const buildMessage = (message: string, count: number, additionalMessages: string[]): string => {
          if (messageCount[message] > 1) {
            let builtMessage = `${message} | (${count})\n`;
            additionalMessages.forEach(additionalMessage => {
              builtMessage += `- ${additionalMessage.split('/').slice(4).join('/')}\n`;
            });
            return builtMessage;
          } else {
            return `${message} `;
          }
        };

        let displayMessage = buildMessage(message, messageCount[message], additionalMessages[message]);

        if (
          response?.code === 'ERR_NETWORK' ||
          (response !== null &&
            typeof response?.response?.data !== 'string' &&
            response?.response?.data?.code !== undefined &&
            response?.response?.data?.code >= 400 &&
            response?.response?.data?.code < 500)
        ) {
          displayMessage = `${message}`;
          if (messageCount[message] > 1) {
            displayMessage += `    (${messageCount[message]})`;
          }
        }

        if (enqueueSnackbar) {
          snackbarKey = enqueueSnackbar(displayMessage, {
            variant,
            anchorOrigin: {
              vertical: 'bottom',
              horizontal: 'right',
            },
            autoHideDuration,
          });
          messageCount[message] = 0;
          additionalMessages[message] = [];
          timeoutId[message] = null;
        }
      }, 500);
    }
  }

  return snackbarKey;
};
