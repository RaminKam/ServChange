export const state = {
  isAuthenticated: localStorage.getItem("auth"),
  user: null,
  error: null,
  loading: false,
  categoires:[],
  univers:[],
  common: {categories:[],univers:[]}
};
