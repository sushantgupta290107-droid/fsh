-- PRIZZ demo schema (no Supabase Auth)
-- Run in Supabase SQL Editor, then enable Realtime on: requests, bids, bookings, messages, notifications

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS provider_profiles (
  id TEXT PRIMARY KEY,
  display_name TEXT NOT NULL,
  verified BOOLEAN DEFAULT true,
  rating NUMERIC(3,2) DEFAULT 4.5,
  completed_jobs INT DEFAULT 0,
  location TEXT,
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS requests (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  customer_id TEXT NOT NULL DEFAULT 'demo_customer_001',
  category TEXT NOT NULL,
  title TEXT NOT NULL,
  description TEXT,
  quantity INT,
  scheduled_date DATE,
  start_time TEXT,
  duration_hours NUMERIC(4,1),
  location TEXT NOT NULL,
  image_url TEXT,
  budget NUMERIC(12,2),
  let_providers_set_price BOOLEAN DEFAULT false,
  urgent BOOLEAN DEFAULT false,
  status TEXT NOT NULL DEFAULT 'open',
  accepted_bid_id UUID,
  bidding_ends_at TIMESTAMPTZ,
  is_demo_seed BOOLEAN DEFAULT false,
  created_at TIMESTAMPTZ DEFAULT now(),
  updated_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS bids (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  request_id UUID NOT NULL REFERENCES requests(id) ON DELETE CASCADE,
  provider_id TEXT NOT NULL REFERENCES provider_profiles(id),
  amount NUMERIC(12,2) NOT NULL,
  eta_minutes INT,
  availability TEXT,
  message TEXT,
  included_services TEXT[] DEFAULT '{}',
  status TEXT NOT NULL DEFAULT 'pending',
  is_new BOOLEAN DEFAULT true,
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS bookings (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  request_id UUID NOT NULL REFERENCES requests(id) ON DELETE CASCADE,
  bid_id UUID NOT NULL REFERENCES bids(id),
  customer_id TEXT NOT NULL,
  provider_id TEXT NOT NULL,
  status TEXT NOT NULL DEFAULT 'provider_selected',
  total_amount NUMERIC(12,2) NOT NULL,
  platform_fee NUMERIC(12,2) NOT NULL,
  provider_amount NUMERIC(12,2) NOT NULL,
  started_at TIMESTAMPTZ,
  completed_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ DEFAULT now(),
  updated_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS payments (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  booking_id UUID NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
  amount NUMERIC(12,2) NOT NULL,
  platform_fee NUMERIC(12,2) NOT NULL,
  provider_amount NUMERIC(12,2) NOT NULL,
  status TEXT NOT NULL DEFAULT 'mock_pending',
  payment_method TEXT DEFAULT 'mock_test',
  paid_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS proof_submissions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  booking_id UUID NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
  photo_data TEXT,
  note TEXT,
  submitted_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS ratings (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  booking_id UUID NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
  stars INT CHECK (stars >= 1 AND stars <= 5),
  comment TEXT,
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS messages (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  booking_id UUID NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
  sender_id TEXT NOT NULL,
  body TEXT NOT NULL,
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS notifications (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id TEXT NOT NULL,
  title TEXT NOT NULL,
  body TEXT,
  read BOOLEAN DEFAULT false,
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_requests_customer ON requests(customer_id);
CREATE INDEX IF NOT EXISTS idx_requests_status ON requests(status);
CREATE INDEX IF NOT EXISTS idx_bids_request ON bids(request_id);
CREATE INDEX IF NOT EXISTS idx_bookings_request ON bookings(request_id);
CREATE INDEX IF NOT EXISTS idx_messages_booking ON messages(booking_id);

ALTER TABLE requests ENABLE ROW LEVEL SECURITY;
ALTER TABLE bids ENABLE ROW LEVEL SECURITY;
ALTER TABLE bookings ENABLE ROW LEVEL SECURITY;
ALTER TABLE payments ENABLE ROW LEVEL SECURITY;
ALTER TABLE proof_submissions ENABLE ROW LEVEL SECURITY;
ALTER TABLE ratings ENABLE ROW LEVEL SECURITY;
ALTER TABLE messages ENABLE ROW LEVEL SECURITY;
ALTER TABLE notifications ENABLE ROW LEVEL SECURITY;
ALTER TABLE provider_profiles ENABLE ROW LEVEL SECURITY;

CREATE POLICY "demo_all_requests" ON requests FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "demo_all_bids" ON bids FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "demo_all_bookings" ON bookings FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "demo_all_payments" ON payments FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "demo_all_proof" ON proof_submissions FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "demo_all_ratings" ON ratings FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "demo_all_messages" ON messages FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "demo_all_notifications" ON notifications FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "demo_all_providers" ON provider_profiles FOR ALL USING (true) WITH CHECK (true);

-- Enable Realtime (run if tables are not already in publication)
ALTER PUBLICATION supabase_realtime ADD TABLE requests;
ALTER PUBLICATION supabase_realtime ADD TABLE bids;
ALTER PUBLICATION supabase_realtime ADD TABLE bookings;
ALTER PUBLICATION supabase_realtime ADD TABLE messages;
ALTER PUBLICATION supabase_realtime ADD TABLE notifications;