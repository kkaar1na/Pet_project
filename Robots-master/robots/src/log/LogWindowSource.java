package log;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Что починить:
 * 1. Этот класс порождает утечку ресурсов (связанные слушатели оказываются
 * удерживаемыми в памяти)
 * 2. Этот класс хранит активные сообщения лога, но в такой реализации он
 * их лишь накапливает. Надо же, чтобы количество сообщений в логе было ограничено
 * величиной m_iQueueLength (т.е. реально нужна очередь сообщений
 * ограниченного размера)
 */
public class LogWindowSource
{
    private final int m_iQueueLength;

    private final ArrayDeque<LogEntry> m_messages;
    private final ArrayList<WeakReference<LogChangeListener>> m_listeners;
    private volatile LogChangeListener[] m_activeListeners;

    public LogWindowSource(int iQueueLength)
    {
        m_iQueueLength = iQueueLength;
        m_messages = new ArrayDeque<LogEntry>(iQueueLength);
        m_listeners = new ArrayList<WeakReference<LogChangeListener>>();
    }

    public void registerListener(LogChangeListener listener)
    {
        synchronized(m_listeners)
        {
            m_listeners.add(new WeakReference<LogChangeListener>(listener));
            m_activeListeners = null;
        }
    }

    public void unregisterListener(LogChangeListener listener)
    {
        synchronized(m_listeners)
        {
            Iterator<WeakReference<LogChangeListener>> it = m_listeners.iterator();
            while (it.hasNext())
            {
                LogChangeListener l = it.next().get();
                if (l == null || l == listener)
                {
                    it.remove();
                }
            }
            m_activeListeners = null;
        }
    }

    public void append(LogLevel logLevel, String strMessage)
    {
        LogEntry entry = new LogEntry(logLevel, strMessage);

        synchronized (m_messages)
        {
            if (m_messages.size() >= m_iQueueLength)
            {
                m_messages.removeFirst();
            }
            m_messages.addLast(entry);
        }

        LogChangeListener [] activeListeners = m_activeListeners;
        if (activeListeners == null)
        {
            synchronized (m_listeners)
            {
                if (m_activeListeners == null)
                {
                    ArrayList<LogChangeListener> strongRefs = new ArrayList<LogChangeListener>();

                    Iterator<WeakReference<LogChangeListener>> it = m_listeners.iterator();
                    while (it.hasNext())
                    {
                        LogChangeListener listener = it.next().get();
                        if (listener == null)
                        {
                            it.remove();
                        }
                        else
                        {
                            strongRefs.add(listener);
                        }
                    }

                    activeListeners = strongRefs.toArray(new LogChangeListener[0]);
                    m_activeListeners = activeListeners;
                }
                else
                {
                    activeListeners = m_activeListeners;
                }
            }
        }

        for (LogChangeListener listener : activeListeners)
        {
            listener.onLogChanged();
        }
    }

    public int size()
    {
        synchronized (m_messages)
        {
            return m_messages.size();
        }
    }

    public Iterable<LogEntry> range(int startFrom, int count)
    {
        synchronized (m_messages)
        {
            if (startFrom < 0 || startFrom >= m_messages.size())
            {
                return Collections.emptyList();
            }

            ArrayList<LogEntry> list = new ArrayList<LogEntry>(m_messages);
            int indexTo = Math.min(startFrom + count, list.size());
            return list.subList(startFrom, indexTo);
        }
    }

    public Iterable<LogEntry> all()
    {
        synchronized (m_messages)
        {
            return new ArrayList<LogEntry>(m_messages);
        }
    }
}
