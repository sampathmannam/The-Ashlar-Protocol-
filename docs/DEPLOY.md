# Deploy the petition page (free, zero backend)

The page at `petition/index.html` is a self-contained static site wired for
**Netlify Forms** — submissions are captured with no server and no cost.

`netlify.toml` at the repo root publishes the `petition/` folder as the site root,
so the page serves at `/` and the share image at `/og.png`.

---

## Step 0 — set your domain in the page (do this before or right after deploy)
Open `petition/index.html` and replace **`theashlarprotocol.org`** with your live
domain in these 4 tags (search for it):
- `<link rel="canonical" ...>`
- `og:url`
- `og:image`
- `twitter:image`

Until this matches your live URL, social link-previews won't load the share image.
(The page itself works regardless — this only affects previews on X, iMessage, Slack, etc.)

---

## Deploy — pick one

### Option A · Netlify Drop (fastest, ~30 seconds, no account link required)
1. Go to **https://app.netlify.com/drop**
2. Drag the **`petition/`** folder onto the page
3. You get a live `*.netlify.app` URL immediately; the form is detected automatically
4. Claim the site to a free Netlify account so you can see submissions

### Option B · Connect the Git repo (auto-redeploys on every push)
1. Push this repo to GitHub (if not already), then Netlify → **Add new site → Import from Git**
2. Pick `The-Ashlar-Protocol-`
3. `netlify.toml` already sets publish = `petition/` with no build command — just confirm and deploy

---

## After it's live — hear about petitions
- Submissions land under **Netlify dashboard → Forms → "petition"**
- Turn on email alerts: **Forms → Settings → Form notifications → add email notification**
  (otherwise you must check the dashboard manually)
- **Submit a test petition yourself** and confirm it appears

---

## Custom domain (optional, when ready)
1. Buy a domain (Namecheap, Cloudflare, Porkbun, etc.)
2. Netlify → your site → **Domain management → Add a domain**
3. Point DNS one of two ways:
   - **Easiest:** use **Netlify DNS** — change your registrar's nameservers to the ones Netlify shows
   - **Or keep your DNS:** add an `A` record for the apex to Netlify's load balancer IP (`75.2.60.5`) and a `CNAME` for `www` → `<your-site>.netlify.app` (Netlify shows exact values)
4. Netlify provisions HTTPS automatically (Let's Encrypt) once DNS resolves
5. **Then redo Step 0** with the final domain and redeploy so previews resolve

---

## Verify the share image
After deploy + domain, paste your URL into a link-preview debugger to confirm the card:
- X: https://cards-dev.twitter.com/validator
- Facebook/OG: https://developers.facebook.com/tools/debug/
- Or just paste the link into Slack / iMessage and look
