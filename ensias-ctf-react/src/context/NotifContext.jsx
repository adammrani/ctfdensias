import { createContext, useContext, useState, useCallback, useRef } from 'react';

const NotifContext = createContext(null);

export function NotifProvider({ children }) {
  const [notif, setNotif] = useState({ msg: '', type: '', visible: false });
  const timerRef = useRef(null);

  const showNotification = useCallback((msg, type = 'info') => {
    clearTimeout(timerRef.current);
    setNotif({ msg, type, visible: true });
    timerRef.current = setTimeout(() => {
      setNotif(n => ({ ...n, visible: false }));
    }, 3500);
  }, []);

  return (
    <NotifContext.Provider value={{ showNotification }}>
      {children}
      <div className={`notification ${notif.type} ${notif.visible ? 'show' : ''}`}>
        {notif.msg}
      </div>
    </NotifContext.Provider>
  );
}

export const useNotif = () => useContext(NotifContext);
