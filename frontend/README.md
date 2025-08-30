# Ticket System Frontend

Next.js frontend application for the Ticket Management System.

## Features

- **Modern UI**: Built with Next.js 14 and Tailwind CSS
- **TypeScript**: Full type safety throughout the application
- **Authentication**: JWT-based authentication with protected routes
- **Role-based Access**: Different interfaces for Users, Support Agents, and Admins
- **Responsive Design**: Mobile-first responsive design
- **Real-time Updates**: Live updates for ticket status changes
- **File Upload**: Drag-and-drop file attachments
- **Search & Filter**: Advanced filtering and search capabilities

## Tech Stack

- **Next.js 14** with App Router
- **TypeScript** for type safety
- **Tailwind CSS** for styling
- **React Hook Form** for form management
- **Zod** for schema validation
- **React Query** for data fetching
- **Axios** for HTTP requests

## Getting Started

### Prerequisites

- Node.js 18+
- npm 8+

### Installation

1. Install dependencies:
```bash
npm install
```

2. Start the development server:
```bash
npm run dev
```

The application will be available at `http://localhost:3000`

### Environment Variables

Create a `.env.local` file in the root directory:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
NEXT_PUBLIC_APP_NAME=Ticket System
```

## Project Structure

```
src/
├── app/                    # Next.js App Router pages
│   ├── (auth)/            # Authentication pages
│   ├── dashboard/         # User dashboard
│   ├── admin/             # Admin panel
│   ├── tickets/           # Ticket management
│   ├── layout.tsx         # Root layout
│   └── page.tsx           # Home page
├── components/            # Reusable components
│   ├── ui/               # Basic UI components
│   ├── forms/            # Form components
│   ├── layout/           # Layout components
│   └── tickets/          # Ticket-specific components
├── lib/                  # Utility functions
│   ├── api.ts           # API client
│   ├── auth.ts          # Authentication utilities
│   ├── utils.ts         # General utilities
│   └── validations.ts   # Zod schemas
└── types/               # TypeScript type definitions
```

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run start` - Start production server
- `npm run lint` - Run ESLint
- `npm run type-check` - Run TypeScript compiler

## Features by Role

### Regular Users
- Create and manage their own tickets
- Add comments to their tickets
- Upload file attachments
- Rate resolved tickets
- View ticket history

### Support Agents
- View assigned tickets
- Update ticket status
- Add comments to any ticket
- Reassign tickets
- Access ticket analytics

### Administrators
- Full user management
- System-wide ticket oversight
- Role assignment
- System configuration
- Advanced analytics

## Building for Production

```bash
npm run build
npm run start
```

## Deployment

The application can be deployed to any platform that supports Next.js:
- Vercel (recommended)
- Netlify
- AWS Amplify
- Docker containers
