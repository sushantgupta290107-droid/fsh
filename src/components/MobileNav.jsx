import { Link, useLocation } from 'react-router-dom'

const customerLinks = [
  { to: '/customer', label: 'Home' },
  { to: '/customer/requests', label: 'Requests' },
  { to: '/customer/messages', label: 'Messages' },
  { to: '/customer/profile', label: 'Profile' },
]

const providerLinks = [
  { to: '/provider', label: 'Jobs' },
  { to: '/provider/active', label: 'Active' },
  { to: '/provider/earnings', label: 'Earnings' },
  { to: '/provider/profile', label: 'Profile' },
]

export default function MobileNav({ variant = 'customer' }) {
  const location = useLocation()
  const links = variant === 'provider' ? providerLinks : customerLinks

  return (
    <nav className="md:hidden fixed bottom-0 inset-x-0 border-t-2 border-ink bg-paper z-50">
      <ul className="grid grid-cols-4">
        {links.map((l) => {
          const active = location.pathname === l.to
          return (
            <li key={l.to}>
              <Link
                to={l.to}
                className={`block text-center py-3 text-xs font-bold uppercase ${
                  active ? 'bg-ink text-paper' : 'text-ink'
                }`}
              >
                {l.label}
              </Link>
            </li>
          )
        })}
      </ul>
    </nav>
  )
}
