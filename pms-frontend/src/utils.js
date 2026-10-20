export const money = (n) => "₹" + Number(n ?? 0).toFixed(2);

export function formatDate(d) {
  if (!d) return "";
  const date = Array.isArray(d)
    ? new Date(d[0], d[1] - 1, d[2], d[3] || 0, d[4] || 0, d[5] || 0)
    : new Date(d);
  return date.toLocaleString();
}
