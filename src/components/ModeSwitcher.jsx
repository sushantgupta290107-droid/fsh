import { useMode } from '../context/ModeContext'
import { MODES } from '../lib/constants'
import { useNavigate } from 'react-router-dom'

const tabs = [
  { id: MODES.CUSTOMER, label: 'CUSTOMER', path: '/customer' },
  { id: MODES.PROVIDER, label: 'PROVIDER', path: '/provider' },
  { id: MODES.ADMIN, label: 'ADMIN', path: '/admin' },
]

export default function ModeSwitcher({ vertical = false }) {
  const { mode, setMode } = useMode()
  const navigate = useNavigate()

  return (
    <div
      className={`flex ${vertical ? 'flex-col gap-1' : 'flex-wrap gap-1'} border-2 border-ink bg-paper p-1 rounded-tight`}
      role="tablist"
      aria-label="Demo mode"
    >
      {tabs.map((t) => (
        <button
          key={t.id}
          type="button"
          role="tab"
          aria-selected={mode === t.id}
          onClick={() => {
            setMode(t.id)
            navigate(t.path)
          }}
          className={`px-3 py-1.5 text-xs font-bold uppercase tracking-wider transition ${
            mode === t.id
              ? 'bg-ink text-paper'
              : 'text-ink hover:bg-border/50'
          }`}
        >
          {t.label}
        </button>
      ))}
    </div>
  )
}
