export default function LiveIndicator({ label = 'LIVE' }) {
  return (
    <span className="inline-flex items-center gap-2 font-bold uppercase text-sm tracking-widest">
      <span className="relative flex h-2.5 w-2.5">
        <span className="animate-pulse-live absolute inline-flex h-full w-full rounded-full bg-prizz opacity-75" />
        <span className="relative inline-flex h-2.5 w-2.5 rounded-full bg-prizz border border-ink" />
      </span>
      <span className="text-ink">{label}</span>
    </span>
  )
}
