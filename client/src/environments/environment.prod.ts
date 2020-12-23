// queste configurazioni saranno usate solo in fase di produzione,
// la configurazione di NGINX provvedera a proxare queste route al container del backend
export const environment = {
  production: false,
  apiUrl: "/api/",
  imgUrl: "/VM_images/" 
};
