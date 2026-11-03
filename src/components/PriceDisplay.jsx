export default function PriceDisplay({ amount, size = 'lg', className = '' }) {
  const formatted =
    typeof amount === 'number'
      ? `₹${amount.toLocaleString('en-IN')}`
      : amount

  const sizes = {
    sm: 'text-2xl',
    md: 'text-4xl',
    lg: 'text-5xl md:text-6xl',
    xl: 'text-6xl md:text-7xl',
  }

  return (
    <span className={`prizz-display ${sizes[size] ?? sizes.lg} text-ink ${className}`}>
      {formatted}
    </span>
  )
}
