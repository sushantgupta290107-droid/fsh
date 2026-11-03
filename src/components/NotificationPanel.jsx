import { useEffect, useState } from 'react'
import { fetchNotifications } from '../lib/api'
import { useMode } from '../context/ModeContext'

export default function NotificationPanel({ compact }) {
  const { identity } = useMode()
  const [items, setItems] = useState([])

  useEffect(() => {
    let cancelled = false
    fetchNotifications(identity)
      .then((data) => {
        if (!cancelled) setItems(data)
      })
      .catch(() => {})
    return () => {
      cancelled = true
    }
  }, [identity])

  if (!items.length) return null

  return (
    <div className={`panel-border ${compact ? 'p-3' : 'p-4'} max-h-48 overflow-y-auto`}>
      <p className="text-xs font-bold uppercase mb-2">Notifications</p>
      <ul className="space-y-2">
        {items.slice(0, 5).map((n) => (
          <li key={n.id} className="text-sm border-l-4 border-prizz pl-2">
            <span className="font-bold">{n.title}</span>
            {n.body && <span className="text-stone"> — {n.body}</span>}
          </li>
        ))}
      </ul>
    </div>
  )
}
