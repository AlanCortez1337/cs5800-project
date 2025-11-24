FROM node:20-alpine

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy all files
COPY . .

# Build argument for API URL
ARG SPRING_BOOT_API_URL
ENV SPRING_BOOT_API_URL=$SPRING_BOOT_API_URL

# Build the app
RUN npm run build

# Expose port
EXPOSE 3000

ENV PORT=3000
ENV HOSTNAME="0.0.0.0"

# Start the app
CMD ["npm", "start"]