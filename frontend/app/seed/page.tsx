"use client";

import { Button } from "@/components/ui/button";
import { useState } from "react";

export default function ReportsPage() {
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  async function createReports() {
    setLoading(true);
    setMessage("");

    const res = await fetch("/api/reports/seed", {
      method: "POST",
    });

    const data = await res.json();
    setMessage(data.message || data.error);
    setLoading(false);
  }

  async function clearReports() {
    setLoading(true);
    setMessage("");

    const res = await fetch("/api/reports/seed/clear", {
      method: "DELETE",
    });

    const data = await res.json();
    setMessage(data.message || data.error);
    setLoading(false);
  }

  async function createUsers() {
    setLoading(true);
    setMessage("");

    const res = await fetch("/api/user/seed", {
      method: "POST",
    });

    const data = await res.json();
    setMessage(data.message || data.error);
    setLoading(false);
  }

  async function clearUsers() {
    setLoading(true);
    setMessage("");

    const res = await fetch("/api/user/seed/clear", {
      method: "DELETE",
    });

    const data = await res.json();
    setMessage(data.message || data.error);
    setLoading(false);
  }

  return (
    <div className="space-y-4 p-6">
      <h1 className="text-2xl font-bold">Report Controls</h1>

      <div className="flex gap-4">
        <Button onClick={createReports} disabled={loading}>
          Seed Reports
        </Button>

        <Button variant="destructive" onClick={clearReports} disabled={loading}>
          Clear Reports
        </Button>
      </div>

      <div className="flex gap-4">
        <Button onClick={createUsers} disabled={loading}>
          Seed Users
        </Button>

        <Button variant="destructive" onClick={clearUsers} disabled={loading}>
          Clear Users
        </Button>
      </div>

      {message && (
        <p className="text-sm text-muted-foreground mt-4">{message}</p>
      )}
    </div>
  );
}