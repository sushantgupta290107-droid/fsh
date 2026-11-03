# PRIZZ

**Put the problem out. Let the market answer.**

React + JSX + Vite + Tailwind + Supabase demo app (no authentication).

## Setup

1. Install dependencies:

```bash
npm install
```

2. Create a Supabase project at [supabase.com](https://supabase.com).

3. Copy `.env.example` to `.env` and set:

- `VITE_SUPABASE_URL`
- `VITE_SUPABASE_ANON_KEY`

4. In the Supabase **SQL Editor**, run the full contents of `supabase/schema.sql`.

5. In **Database → Replication**, enable Realtime for:

- `requests`
- `bids`
- `bookings`
- `messages`
- `notifications`

6. Start the app:

```bash
npm run dev
```

7. Open **Admin → DEMO CONTROLS → RESET DEMO** (confirm) to load the seeded “150 chairs” scenario.

## Demo flow

1. Customer → create request or use seeded live request  
2. Provider → submit offer (inserts to Supabase)  
3. Customer → Live PRIZZ receives offer via Realtime  
4. Accept → booking + mock payment → job → proof → completion → rating  
5. Admin → **RESET DEMO** restores seed in the database  

Demo identities: `demo_customer_001`, `demo_provider_001`, `demo_admin_001`.

## Stack

- React 18, JSX, React Router  
- Tailwind (PRIZZ palette tokens)  
- Supabase Postgres + Realtime (no Supabase Auth)
