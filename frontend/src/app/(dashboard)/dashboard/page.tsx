'use client';

import React, { useEffect, useState } from 'react';
import Link from 'next/link';
import { useAuth } from '@/lib/auth-context';
import { api } from '@/lib/api';
import { Card, CardBody, CardHeader } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import { 
  Ticket, 
  Plus, 
  Clock, 
  CheckCircle, 
  AlertTriangle,
  TrendingUp,
  Users,
  BarChart3
} from 'lucide-react';
import { formatDate, getStatusColor, getPriorityColor } from '@/lib/utils';

interface DashboardStats {
  totalTickets: number;
  openTickets: number;
  inProgressTickets: number;
  resolvedTickets: number;
  myTickets: number;
  assignedTickets?: number;
}

interface RecentTicket {
  id: string;
  subject: string;
  status: string;
  priority: string;
  createdAt: string;
  createdBy: {
    firstName: string;
    lastName: string;
  };
}

export default function DashboardPage() {
  const { user } = useAuth();
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [recentTickets, setRecentTickets] = useState<RecentTicket[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      // Load stats and recent tickets
      const [statsResponse, ticketsResponse] = await Promise.all([
        api.get('/admin/stats'), // This will be filtered by backend based on user role
        api.get('/tickets?size=5&sort=createdAt&direction=desc')
      ]);

      setStats(statsResponse.data);
      setRecentTickets(ticketsResponse.data.content);
    } catch (error) {
      console.error('Failed to load dashboard data:', error);
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  const isAdmin = user?.role === 'ADMIN';
  const isSupportAgent = user?.role === 'SUPPORT_AGENT' || user?.role === 'ADMIN';

  return (
    <div className="space-y-6">
      {/* Welcome Header */}
      <div className="bg-white shadow rounded-lg p-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">
              Welcome back, {user?.firstName}!
            </h1>
            <p className="text-gray-600">
              Here's what's happening with your tickets today.
            </p>
          </div>
          <Link href="/tickets/create">
            <Button>
              <Plus className="h-4 w-4 mr-2" />
              Create Ticket
            </Button>
          </Link>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <Card>
          <CardBody className="flex items-center">
            <div className="flex-shrink-0">
              <Ticket className="h-8 w-8 text-blue-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-500">Total Tickets</p>
              <p className="text-2xl font-semibold text-gray-900">
                {stats?.totalTickets || 0}
              </p>
            </div>
          </CardBody>
        </Card>

        <Card>
          <CardBody className="flex items-center">
            <div className="flex-shrink-0">
              <Clock className="h-8 w-8 text-yellow-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-500">Open</p>
              <p className="text-2xl font-semibold text-gray-900">
                {stats?.openTickets || 0}
              </p>
            </div>
          </CardBody>
        </Card>

        <Card>
          <CardBody className="flex items-center">
            <div className="flex-shrink-0">
              <TrendingUp className="h-8 w-8 text-orange-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-500">In Progress</p>
              <p className="text-2xl font-semibold text-gray-900">
                {stats?.inProgressTickets || 0}
              </p>
            </div>
          </CardBody>
        </Card>

        <Card>
          <CardBody className="flex items-center">
            <div className="flex-shrink-0">
              <CheckCircle className="h-8 w-8 text-green-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-500">Resolved</p>
              <p className="text-2xl font-semibold text-gray-900">
                {stats?.resolvedTickets || 0}
              </p>
            </div>
          </CardBody>
        </Card>
      </div>

      {/* Quick Actions */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <Card>
          <CardHeader>
            <h3 className="text-lg font-medium text-gray-900">Quick Actions</h3>
          </CardHeader>
          <CardBody className="space-y-3">
            <Link href="/tickets/create" className="block">
              <Button variant="outline" className="w-full justify-start">
                <Plus className="h-4 w-4 mr-2" />
                Create New Ticket
              </Button>
            </Link>
            <Link href="/tickets" className="block">
              <Button variant="outline" className="w-full justify-start">
                <Ticket className="h-4 w-4 mr-2" />
                View My Tickets
              </Button>
            </Link>
            {isSupportAgent && (
              <Link href="/tickets/assigned" className="block">
                <Button variant="outline" className="w-full justify-start">
                  <Users className="h-4 w-4 mr-2" />
                  Assigned Tickets
                </Button>
              </Link>
            )}
            {isAdmin && (
              <Link href="/admin" className="block">
                <Button variant="outline" className="w-full justify-start">
                  <BarChart3 className="h-4 w-4 mr-2" />
                  Admin Panel
                </Button>
              </Link>
            )}
          </CardBody>
        </Card>

        {/* Recent Tickets */}
        <Card className="lg:col-span-2">
          <CardHeader>
            <div className="flex items-center justify-between">
              <h3 className="text-lg font-medium text-gray-900">Recent Tickets</h3>
              <Link href="/tickets" className="text-sm text-primary-600 hover:text-primary-500">
                View all
              </Link>
            </div>
          </CardHeader>
          <CardBody>
            {recentTickets.length > 0 ? (
              <div className="space-y-4">
                {recentTickets.map((ticket) => (
                  <div key={ticket.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                    <div className="flex-1">
                      <Link href={`/tickets/${ticket.id}`} className="block">
                        <p className="font-medium text-gray-900 hover:text-primary-600">
                          {ticket.subject}
                        </p>
                        <p className="text-sm text-gray-500">
                          by {ticket.createdBy.firstName} {ticket.createdBy.lastName} • {formatDate(ticket.createdAt)}
                        </p>
                      </Link>
                    </div>
                    <div className="flex items-center space-x-2">
                      <Badge className={getPriorityColor(ticket.priority)}>
                        {ticket.priority}
                      </Badge>
                      <Badge className={getStatusColor(ticket.status)}>
                        {ticket.status.replace('_', ' ')}
                      </Badge>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="text-center py-6">
                <Ticket className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                <p className="text-gray-500">No recent tickets</p>
                <Link href="/tickets/create">
                  <Button className="mt-2">Create your first ticket</Button>
                </Link>
              </div>
            )}
          </CardBody>
        </Card>
      </div>

      {/* Priority Alerts */}
      {isSupportAgent && (
        <Card>
          <CardHeader>
            <h3 className="text-lg font-medium text-gray-900 flex items-center">
              <AlertTriangle className="h-5 w-5 text-orange-500 mr-2" />
              Attention Required
            </h3>
          </CardHeader>
          <CardBody>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="text-center p-4 bg-red-50 rounded-lg">
                <p className="text-2xl font-bold text-red-600">0</p>
                <p className="text-sm text-red-800">Urgent Tickets</p>
              </div>
              <div className="text-center p-4 bg-yellow-50 rounded-lg">
                <p className="text-2xl font-bold text-yellow-600">0</p>
                <p className="text-sm text-yellow-800">Unassigned</p>
              </div>
              <div className="text-center p-4 bg-orange-50 rounded-lg">
                <p className="text-2xl font-bold text-orange-600">0</p>
                <p className="text-sm text-orange-800">Overdue</p>
              </div>
            </div>
          </CardBody>
        </Card>
      )}
    </div>
  );
}
