#!/bin/sh
set -e

INDEX_HTML="/usr/share/nginx/html/index.html"
MARKER="patch-title-injected"

if [ ! -f "$INDEX_HTML" ]; then
  echo "[patch-title] index.html not found at $INDEX_HTML"
  exit 0
fi

if grep -q "$MARKER" "$INDEX_HTML"; then
  echo "[patch-title] already patched, skipping"
  exit 0
fi

SNIPPET=$(cat <<'EOF'
<script id="patch-title-injected">
(function () {
  function updateTitle() {
    var select = document.querySelector("select");
    if (select && select.selectedOptions[0]) {
      document.title = select.selectedOptions[0].text;
    }
  }
  var observer = new MutationObserver(function () {
    var select = document.querySelector("select");
    if (select && !select.dataset.titleBound) {
      select.dataset.titleBound = "true";
      select.addEventListener("change", updateTitle);
      updateTitle();
    }
  });
  observer.observe(document.body, { childList: true, subtree: true });
})();
</script>
EOF
)

awk -v snippet="$SNIPPET" '
  /<\/body>/ { print snippet }
  { print }
' "$INDEX_HTML" > "${INDEX_HTML}.tmp" && mv "${INDEX_HTML}.tmp" "$INDEX_HTML"

echo "[patch-title] patched $INDEX_HTML"
