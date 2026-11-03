import { Link } from 'react-router-dom'

export function SupabaseBanner() {
  return null
}

export function SetupSeedHint() {
  return (
    <p className="text-sm text-stone mt-4">
      First time? Open <Link to="/admin" className="underline font-semibold">Admin</Link> and use{' '}
      <strong>RESET DEMO</strong> to load the seeded scenario.
    </p>
  )
}
