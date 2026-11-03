export default function Countdown({ value, label }) {
  return (
    <div>
      {label && (
        <p className="text-xs font-bold uppercase tracking-widest text-stone mb-1">{label}</p>
      )}
      <p className="prizz-display text-5xl md:text-6xl tabular-nums">{value}</p>
    </div>
  )
}
