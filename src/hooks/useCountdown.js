import { useEffect, useState } from 'react'

export function useCountdown(endIso) {
  const [remaining, setRemaining] = useState('00:00:00')

  useEffect(() => {
    if (!endIso) {
      setRemaining('00:00:00')
      return
    }
    const tick = () => {
      const diff = new Date(endIso).getTime() - Date.now()
      if (diff <= 0) {
        setRemaining('00:00:00')
        return
      }
      const h = Math.floor(diff / 3600000)
      const m = Math.floor((diff % 3600000) / 60000)
      const s = Math.floor((diff % 60000) / 1000)
      setRemaining(
        [h, m, s].map((n) => String(n).padStart(2, '0')).join(':'),
      )
    }
    tick()
    const id = setInterval(tick, 1000)
    return () => clearInterval(id)
  }, [endIso])

  return remaining
}
