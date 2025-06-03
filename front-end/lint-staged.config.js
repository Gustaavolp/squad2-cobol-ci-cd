module.exports = {
  '*.{ts,js}': [
    'eslint --fix',
    'prettier --write'
  ],
  '*.{html,css,scss,json,md}': [
    'prettier --write'
  ]
};