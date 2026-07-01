import { ElMessage } from 'element-plus'

/**
 * Download a file as a blob using fetch with auth headers.
 * Bypasses the Axios JSON interceptor for binary responses.
 *
 * @param {string} url - The download URL (relative like /api/...)
 * @param {string} filename - The filename to save as
 * @param {string} token - The Bearer token for auth
 */
export async function downloadBlob(url, filename, token) {
  try {
    const response = await fetch(url, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
    if (!response.ok) {
      const text = await response.text()
      throw new Error(text || `HTTP ${response.status}`)
    }
    const blob = await response.blob()
    const blobUrl = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = blobUrl
    a.download = filename
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(blobUrl)
    ElMessage.success('下载成功')
  } catch (e) {
    ElMessage.error('下载失败: ' + e.message)
  }
}

/**
 * Fetch a blob URL for inline display (e.g., images).
 *
 * @param {string} url - The URL to fetch
 * @param {string} token - The Bearer token
 * @returns {Promise<string>} - Object URL for use in <img src>
 */
export async function fetchBlobUrl(url, token) {
  const response = await fetch(url, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`)
  }
  const blob = await response.blob()
  return URL.createObjectURL(blob)
}
