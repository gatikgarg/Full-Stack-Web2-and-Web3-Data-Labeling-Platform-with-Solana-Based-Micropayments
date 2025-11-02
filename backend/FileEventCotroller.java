import React, { useEffect, useState } from 'react';
import { Bell, X, FileText, UserPlus, CheckCircle, XCircle } from 'lucide-react';
import './AdminNotification.css';
import notificationService from '../../../services/notificationService';

const AdminNotification = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);

  const iconFor = (type) => {
    if (type === 'subscription') return UserPlus;
    if (type === 'accepted') return CheckCircle;
    if (type === 'rejected') return XCircle;
    return FileText;
  };

  const resolveEmailFromJWT = () => {
    const token = (typeof sessionStorage !== 'undefined' && sessionStorage.getItem('token'))
      || (typeof localStorage !== 'undefined' && localStorage.getItem('token'))
      || null;
    if (!token) return '';
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.email || payload.sub || '';
    } catch {
      return '';
    }
  };

  const adminEmail = resolveEmailFromJWT();

  const load = async () => {
    try {
      if (!adminEmail) { setNotifications([]); return; }
      const list = await notificationService.getForUser(adminEmail);
      const sorted = [...list].sort((a,b) => new Date(b.createdAt||b.time||0) - new Date(a.createdAt||a.time||0));
      setNotifications(sorted.map(n => ({
        id: n.id,
        type: n.type || 'file',
        title: n.title,
        message: n.message,
        createdAt: n.createdAt || n.time,
        isNew: n.isNew ?? !n.read
      })));
    } catch {
      setNotifications([]);
    }
  };

  useEffect(() => { load(); }, [adminEmail]);

  const unreadCount = notifications.filter(n => n.isNew).length;

  const markAsRead = async (id) => {
    try { await notificationService.markRead(id); } catch {}
    setNotifications(prev => prev.map(n => n.id === id ? { ...n, isNew: false } : n));
  };

  const markAllAsRead = async () => {
    try { if (adminEmail) await notificationService.markAllRead(adminEmail); } catch {}
    setNotifications(prev => prev.map(n => ({ ...n, isNew: false })));
  };

  const removeNotification = async (id) => {
    try { await notificationService.remove(id); } catch {}
    setNotifications(prev => prev.filter(n => n.id !== id));
  };

  return (
    <div className="admin-notif-container">
      <div className="admin-notif-trigger" onClick={() => setIsOpen(!isOpen)}>
        <Bell size={20} />
        {unreadCount > 0 && (
          <span className="admin-notif-badge">{unreadCount}</span>
        )}
      </div>

      {isOpen && (
        <div className="admin-notif-dropdown">
          <div className="admin-notif-header">
            <h5 className="admin-notif-title">Notifications</h5>
            {unreadCount > 0 && (
              <button className="admin-mark-all" onClick={markAllAsRead}>
                Mark all read
              </button>
            )}
          </div>

          <div className="admin-notif-list">
            {notifications.length === 0 ? (
              <div className="admin-no-notif">
                <Bell size={40} />
                <p>No notifications</p>
              </div>
            ) : (
              notifications.map(n => {
                const Icon = iconFor(n.type);
                return (
                  <div
                    key={n.id}
                    className={`admin-notif-item ${n.isNew ? 'unread' : ''}`}
                    onClick={() => markAsRead(n.id)}
                  >
                    <div className={`admin-notif-icon ${n.type}`}>
                      <Icon size={18} />
                    </div>
                    <div className="admin-notif-content">
                      <h6>{n.title}</h6>
                      <p>{n.message}</p>
                      <span className="admin-notif-time">
                        {n.createdAt ? new Date(n.createdAt).toLocaleString() : ''}
                      </span>
                    </div>
                    <button
                      className="admin-notif-close"
                      onClick={(e) => {
                        e.stopPropagation();
                        removeNotification(n.id);
                      }}
                    >
                      <X size={16} />
                    </button>
                  </div>
                );
              })
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminNotification;
