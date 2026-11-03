import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import { DEMO_ADMIN_ID, DEMO_CUSTOMER_ID, DEMO_PROVIDER_ID, MODE_STORAGE_KEY, MODES } from '../lib/constants'

const ModeContext = createContext(null)

export function ModeProvider({ children }) {
  const [mode, setModeState] = useState(() => {
    const stored = localStorage.getItem(MODE_STORAGE_KEY)
    if (stored && Object.values(MODES).includes(stored)) return stored
    return MODES.CUSTOMER
  })

  useEffect(() => {
    localStorage.setItem(MODE_STORAGE_KEY, mode)
  }, [mode])

  const value = useMemo(() => {
    const identity =
      mode === MODES.PROVIDER
        ? DEMO_PROVIDER_ID
        : mode === MODES.ADMIN
          ? DEMO_ADMIN_ID
          : DEMO_CUSTOMER_ID

    return {
      mode,
      setMode: setModeState,
      identity,
      isCustomer: mode === MODES.CUSTOMER,
      isProvider: mode === MODES.PROVIDER,
      isAdmin: mode === MODES.ADMIN,
    }
  }, [mode])

  return <ModeContext.Provider value={value}>{children}</ModeContext.Provider>
}

export function useMode() {
  const ctx = useContext(ModeContext)
  if (!ctx) throw new Error('useMode must be used within ModeProvider')
  return ctx
}
