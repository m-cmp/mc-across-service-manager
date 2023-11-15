import { atom } from 'recoil';

export interface IUser {
  name?: string;
  id?: string;
}

export const userAtom = atom<IUser[]>({
  key: 'userListState',
  default: [],
  // effects: [({ setSelf, onSet }) => {}],
});
