interface ImportMetaEnv {
  readonly VITE_APP_TITLE: string;
  readonly VITE_API_BASE_URL_ORCHESTRATOR: string;
  readonly VITE_API_BASE_URL_MULTICLOUD: string;
  readonly VITE_API_BASE_URL_CONTROLLER: string;
  // 다른 환경 변수들에 대한 타입 정의...
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
