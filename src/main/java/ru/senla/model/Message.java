package ru.senla.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.senla.dto.MessageDTO;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name="messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {

    @Id
    @SequenceGenerator(name = "messages_id_seq",
            sequenceName = "messages_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "messages_id_seq")
    //@GeneratedValue(strategy = GenerationType.IDENTITY) // For tests
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="sender_email")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="receiver_email")
    private User receiver;

    private String content;

    private LocalDateTime time;

    public MessageDTO toDTO() {
        return MessageDTO.builder().sender(this.sender.getName() + ' ' + this.sender.getSurname()
                        + " (" + this.sender.getUsername() + ')').receiver(this.receiver.getName()
                        + ' ' + this.receiver.getSurname() + " (" + this.receiver.getUsername() + ')')
                .content(this.content).time(this.time).build();
    }
}
