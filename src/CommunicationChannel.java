import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class that implements the channel used by wizards and miners to communicate.
 */
public class CommunicationChannel {
	
	private LinkedBlockingQueue<Message> minerBuffer = new LinkedBlockingQueue<>();
	private LinkedBlockingQueue<Message> wizardBuffer = new LinkedBlockingQueue<>();
	ReentrantLock lock = new ReentrantLock();

	/**
	 * Creates a {@code CommunicationChannel} object.
	 */
	public CommunicationChannel() {	
	}

	/**
	 * Puts a message on the miner channel (i.e., where miners write to and wizards
	 * read from).
	 * 
	 * @param message
	 *            message to be put on the channel
	 */
	public void putMessageMinerChannel(Message message) {
		
			try {
				minerBuffer.put(message);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
	}

	/**
	 * Gets a message from the miner channel (i.e., where miners write to and
	 * wizards read from).
	 * 
	 * @return message from the miner channel
	 */
	public Message getMessageMinerChannel() {
		
			Message m = null;
			try {
				m = minerBuffer.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return m;
	
	}

	/**
	 * Puts a message on the wizard channel (i.e., where wizards write to and miners
	 * read from).
	 * 
	 * @param message
	 *            message to be put on the channel
	 */
	public void putMessageWizardChannel(Message message) {
		
		if (message.getData().contains("END")) return;

		//First message already sent
		//So lock is held by current thread
		if (lock.isHeldByCurrentThread()) {
			try {
				//Send second message
				wizardBuffer.put(message);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				//Second message sent, unlock channel
				lock.unlock();
			}

		} else {
			//Acquire lock and send first message
			try {
				lock.lock();
				wizardBuffer.put(message);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets a message from the wizard channel (i.e., where wizards write to and
	 * miners read from).
	 * 
	 * @return message from the miner channel
	 */
	public Message getMessageWizardChannel() {
		Message m = null;
		try {
			m = wizardBuffer.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		return m;
	}
	
	public Message[] getMessageSetWizardChannel() {
		
		//Acquire lock to get 2 consecutive messages
		synchronized (this) {
			Message[] messages = new Message[2];
			try {
				messages[0] = wizardBuffer.take();
				messages[1] = wizardBuffer.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return messages;
		}
	}

}
