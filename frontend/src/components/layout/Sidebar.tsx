'use client';

import React from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useAuth } from '@/lib/auth-context';
import { cn } from '@/lib/utils';
import {
  Home,
  Ticket,
  Plus,
  Users,
  Settings,
  BarChart3,
  FileText,
  UserCheck,
  Shield,
  Clock,
  AlertTriangle,
} from 'lucide-react';

interface SidebarItem {
  name: string;
  href: string;
  icon: React.ComponentType<{ className?: string }>;
  roles?: string[];
  badge?: string;
}

const sidebarItems: SidebarItem[] = [
  {
    name: 'Dashboard',
    href: '/dashboard',
    icon: Home,
  },
  {
    name: 'Create Ticket',
    href: '/tickets/create',
    icon: Plus,
  },
  {
    name: 'My Tickets',
    href: '/tickets',
    icon: Ticket,
  },
  {
    name: 'Assigned Tickets',
    href: '/tickets/assigned',
    icon: UserCheck,
    roles: ['SUPPORT_AGENT', 'ADMIN'],
  },
  {
    name: 'Unassigned',
    href: '/tickets/unassigned',
    icon: Clock,
    roles: ['SUPPORT_AGENT', 'ADMIN'],
  },
  {
    name: 'High Priority',
    href: '/tickets/priority',
    icon: AlertTriangle,
    roles: ['SUPPORT_AGENT', 'ADMIN'],
  },
  {
    name: 'Reports',
    href: '/reports',
    icon: BarChart3,
    roles: ['SUPPORT_AGENT', 'ADMIN'],
  },
  {
    name: 'Users',
    href: '/admin/users',
    icon: Users,
    roles: ['ADMIN'],
  },
  {
    name: 'System Stats',
    href: '/admin/stats',
    icon: FileText,
    roles: ['ADMIN'],
  },
  {
    name: 'Admin Panel',
    href: '/admin',
    icon: Shield,
    roles: ['ADMIN'],
  },
  {
    name: 'Settings',
    href: '/settings',
    icon: Settings,
  },
];

export function Sidebar() {
  const { user } = useAuth();
  const pathname = usePathname();

  const filteredItems = sidebarItems.filter(item => {
    if (!item.roles) return true;
    return user && item.roles.includes(user.role);
  });

  return (
    <div className="hidden md:flex md:w-64 md:flex-col md:fixed md:inset-y-0">
      <div className="flex-1 flex flex-col min-h-0 bg-gray-800">
        <div className="flex-1 flex flex-col pt-5 pb-4 overflow-y-auto">
          <nav className="mt-5 flex-1 px-2 space-y-1">
            {filteredItems.map((item) => {
              const isActive = pathname === item.href || 
                (item.href !== '/dashboard' && pathname.startsWith(item.href));
              
              return (
                <Link
                  key={item.name}
                  href={item.href}
                  className={cn(
                    'group flex items-center px-2 py-2 text-sm font-medium rounded-md transition-colors duration-200',
                    isActive
                      ? 'bg-gray-900 text-white'
                      : 'text-gray-300 hover:bg-gray-700 hover:text-white'
                  )}
                >
                  <item.icon
                    className={cn(
                      'mr-3 flex-shrink-0 h-5 w-5',
                      isActive ? 'text-white' : 'text-gray-400 group-hover:text-white'
                    )}
                  />
                  {item.name}
                  {item.badge && (
                    <span className="ml-auto inline-block py-0.5 px-2 text-xs font-medium rounded-full bg-red-100 text-red-800">
                      {item.badge}
                    </span>
                  )}
                </Link>
              );
            })}
          </nav>
        </div>
        
        {/* User info at bottom */}
        <div className="flex-shrink-0 flex bg-gray-700 p-4">
          <div className="flex items-center">
            <div className="h-8 w-8 bg-primary-600 rounded-full flex items-center justify-center text-white text-sm font-medium">
              {user ? user.firstName.charAt(0) + user.lastName.charAt(0) : 'U'}
            </div>
            <div className="ml-3">
              <p className="text-sm font-medium text-white">
                {user ? `${user.firstName} ${user.lastName}` : 'User'}
              </p>
              <p className="text-xs text-gray-300">
                {user?.role.replace('_', ' ').toLowerCase()}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
