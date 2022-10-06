package me.ghost.pagamento.constructor;

import java.sql.Date;
import java.util.UUID;

import me.ghost.utils.UUIDFetcher;

public class Buy {
	private UUID uuidBuy;
	private String playerName;
	private String packageName;
	private Date duration;
	private PaymentMode paymentMode;
	private boolean payed;

	public Buy(String playerName, String packageName, PaymentMode mode, Date duration) {
		this.playerName = playerName;
		this.packageName = packageName;
		this.duration = duration;
		UUID uuid;
		try {
			uuid = UUIDFetcher.getUUIDOf(playerName);
		} catch (Exception e) {
			System.out.println("Não foi possivel encontrar o UUID de " + playerName);
			uuid = null;
			e.printStackTrace();
		}
		this.uuidBuy = uuid;
		this.paymentMode = mode;
		if (mode == PaymentMode.PAYPAL) {
			this.payed = true;
		} else {
			this.payed = false;
		}
	}

	public boolean isPayed() {
		return payed;
	}

	public void setPayed(boolean payed) {
		this.payed = payed;
	}

	public UUID getUuidBuy() {
		return uuidBuy;
	}

	public String getPlayerName() {
		return playerName;
	}

	public String getPackageName() {
		return packageName;
	}

	public Date getDuration() {
		return duration;
	}

	public PaymentMode getPaymentMode() {
		return paymentMode;
	}
}
