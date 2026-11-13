import React from 'react';
import { youtubeEmbed } from '../../utils/format';

// Shows a lesson according to its content type: text, video, audio or slides.
export const LessonViewer = ({ lesson }) => {
  if (!lesson) return <p className="muted">Select a lesson to start.</p>;

  const { contentType, contentUrl, textContent, instructions, description } = lesson;
  const embed = youtubeEmbed(contentUrl);
  const hasFile = contentType === 'VIDEO' || contentType === 'AUDIO' || contentType === 'SLIDES';

  return (
    <div className="lesson">
      <h3>{lesson.title}</h3>
      {description && <p className="muted">{description}</p>}

      {contentType === 'VIDEO' && contentUrl && (embed ? (
        <iframe className="media" title={lesson.title} src={embed} allowFullScreen />
      ) : (
        <video className="media" src={contentUrl} controls />
      ))}
      {contentType === 'AUDIO' && contentUrl && <audio className="media-audio" src={contentUrl} controls />}
      {contentType === 'SLIDES' && contentUrl && <iframe className="media" title={lesson.title} src={contentUrl} />}
      {hasFile && !contentUrl && <p className="muted">No file has been linked to this lesson yet.</p>}
      {hasFile && contentUrl && (
        <p>
          <a href={contentUrl} target="_blank" rel="noreferrer">
            Open in a new tab
          </a>
        </p>
      )}

      {textContent && <div className="text-content">{textContent}</div>}
      {instructions && (
        <p>
          <b>How to:</b> {instructions}
        </p>
      )}
    </div>
  );
};
