import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Class for a miner.
 */
public class Miner extends Thread {
	private Integer hashCount;
	private Set<Integer> solved;
	private CommunicationChannel channel;
	/**
	 * Creates a {@code Miner} object.
	 * 
	 * @param hashCount
	 *            number of times that a miner repeats the hash operation when
	 *            solving a puzzle.
	 * @param solved
	 *            set containing the IDs of the solved rooms
	 * @param channel
	 *            communication channel between the miners and the wizards
	 */
	public Miner(Integer hashCount, Set<Integer> solved, CommunicationChannel channel) {
		this.hashCount = hashCount;
		this.solved = solved;
		this.channel = channel;
	}

	private static String encryptThisString(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            
            // convert to string
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
            String hex = Integer.toHexString(0xff & messageDigest[i]);
            if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
    
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

	@Override
	public void run() {
		
		while (true) {

			//Get 2 messages from wizards
			Message[] messages = channel.getMessageSetWizardChannel();
			
			//Parent Room
			int parRoom = messages[0].getCurrentRoom();
			
			//Current Room
			int currRoom = messages[1].getCurrentRoom();
			
			//String to be hashed
			String hashString = messages[1].getData();

			//EXIT Message
			if (messages[0].getData().contains("EXIT") || 
				hashString.contains("EXIT")) 
				
				break;

			//Check if room not already solved
			if (!solved.contains(currRoom)) {
				
				//Hash string hashCount times
				for (int i = 0; i < hashCount; i++) {
					hashString = encryptThisString(hashString);
				}

				//Send hashed string message to wizards
				channel.putMessageMinerChannel(new Message(parRoom, currRoom, hashString));
				
				//Mark room as solved
				solved.add(currRoom);

			}

			
		}
	}
}
