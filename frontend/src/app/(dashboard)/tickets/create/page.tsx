'use client';

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { api } from '@/lib/api';
import { createTicketSchema, CreateTicketFormData } from '@/lib/validations';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Card, CardBody, CardHeader } from '@/components/ui/Card';
import { ArrowLeft } from 'lucide-react';
import Link from 'next/link';
import toast from 'react-hot-toast';

export default function CreateTicketPage() {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<CreateTicketFormData>({
    resolver: zodResolver(createTicketSchema),
    defaultValues: {
      priority: 'MEDIUM',
    },
  });

  const onSubmit = async (data: CreateTicketFormData) => {
    setIsLoading(true);
    try {
      const response = await api.post('/tickets', data);
      toast.success('Ticket created successfully!');
      router.push(`/tickets/${response.data.id}`);
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to create ticket');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      {/* Header */}
      <div className="flex items-center space-x-4">
        <Link href="/tickets">
          <Button variant="outline" size="sm">
            <ArrowLeft className="h-4 w-4 mr-2" />
            Back to Tickets
          </Button>
        </Link>
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Create New Ticket</h1>
          <p className="text-gray-600">Describe your issue and we'll help you resolve it</p>
        </div>
      </div>

      {/* Form */}
      <Card>
        <CardHeader>
          <h3 className="text-lg font-medium text-gray-900">Ticket Details</h3>
        </CardHeader>
        <CardBody>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
            <Input
              label="Subject"
              placeholder="Brief description of your issue"
              {...register('subject')}
              error={errors.subject?.message}
            />

            <div>
              <label className="label">Description</label>
              <textarea
                rows={6}
                placeholder="Please provide detailed information about your issue..."
                className="input resize-none"
                {...register('description')}
              />
              {errors.description && (
                <p className="mt-1 text-sm text-red-600">{errors.description.message}</p>
              )}
            </div>

            <div>
              <label className="label">Priority</label>
              <select
                className="input"
                {...register('priority')}
              >
                <option value="LOW">Low - General questions or minor issues</option>
                <option value="MEDIUM">Medium - Standard support requests</option>
                <option value="HIGH">High - Important issues affecting work</option>
                <option value="URGENT">Urgent - Critical issues requiring immediate attention</option>
              </select>
              {errors.priority && (
                <p className="mt-1 text-sm text-red-600">{errors.priority.message}</p>
              )}
            </div>

            <div className="bg-blue-50 border border-blue-200 rounded-md p-4">
              <h4 className="text-sm font-medium text-blue-900 mb-2">Tips for better support:</h4>
              <ul className="text-sm text-blue-800 space-y-1">
                <li>• Be specific about the problem you're experiencing</li>
                <li>• Include steps to reproduce the issue if applicable</li>
                <li>• Mention any error messages you've seen</li>
                <li>• Include relevant screenshots or files if helpful</li>
              </ul>
            </div>

            <div className="flex items-center justify-end space-x-4">
              <Link href="/tickets">
                <Button variant="outline" type="button">
                  Cancel
                </Button>
              </Link>
              <Button type="submit" isLoading={isLoading}>
                Create Ticket
              </Button>
            </div>
          </form>
        </CardBody>
      </Card>
    </div>
  );
}
