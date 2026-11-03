import { Link } from 'react-router-dom'
import { useMode } from '../context/ModeContext'
import { MODES } from '../lib/constants'
import ModeSwitcher from './ModeSwitcher'

export default function AppShell({ children, compact = false }) {
  const { mode } = useMode()

  return (
    <div className="min-h-screen bg-paper text-ink flex flex-col">
      <header className="border-b-2 border-ink bg-paper sticky top-0 z-40">
        <div className={`mx-auto flex items-center justify-between gap-4 px-4 ${compact ? 'py-2' : 'py-3'} max-w-7xl`}>
          <Link to="/" className="flex items-baseline gap-2 group">
            <span className="prizz-display text-3xl md:text-4xl text-ink group-hover:text-prizz transition-colors">
              PRIZZ
            </span>
            {!compact && (
              <span className="hidden sm:inline text-xs text-stone max-w-[200px] leading-tight">
                Put the problem out. Let the market answer.
              </span>
            )}
          </Link>
          <ModeSwitcher />

        </div>
      </header>
      <main className="flex-1">{children}</main>
    </div>
  )
}
